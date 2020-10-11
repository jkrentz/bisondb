(ns bisondb.keyval.core
  "Functions for connecting the an BisonDB (key-value) service via
  Thrift."
  (:use [bisondb.common.domain :only (loaded?)]
        [metrics.timers :only (timer time! time-fn!)]
        [metrics.meters :only (meter mark!)]
        [metrics.core :only (report-to-console)]
        bisondb.common.metrics)
  (:require [jackknife.core :as u]
            [jackknife.logging :as log]
            [bisondb.common.database :as db]
            [bisondb.common.status :as status]
            [bisondb.common.thrift :as thrift]
            [bisondb.common.config :as conf]
            [bisondb.keyval.domain :as dom]
            [bisondb.client :as c]
            [bisondb.common.metadata :as metadata]
            [bisondb.ui.handler :as ui])
  (:import [java.nio ByteBuffer]
           [org.apache.thrift.protocol TBinaryProtocol]
           [org.apache.thrift.transport TTransport]
           [org.apache.thrift TException]
           [bisondb.common.database Database]
           [bisondb.common.domain Domain]
           [bisondb.generated DomainNotFoundException
            DomainNotLoadedException WrongHostException]
           [bisondb.generated.keyval BisonDB$Client
            BisonDB$Iface BisonDB$Processor])
  (:gen-class))

;; ## Metrics

(def hostname (clojure.string/replace (.getCanonicalHostName (java.net.InetAddress/getLocalHost)) #"\." "_"))

(def multi-get-response-time (timer [(str hostname ".bisondb") "keyval" "multi_get_response_time"]))
(def direct-get-response-time (timer [(str hostname ".bisondb") "keyval" "direct_get_response_time"]))

(def get-requests (meter [(str hostname ".bisondb") "keyval" "get_requests"] "requests"))

;; ## Thrift Connection

(defn kv-processor
  "Returns a key-value thrift processor suitable for passing into
  launch-server!"
  [service-handler]
  (BisonDB$Processor. service-handler))

;; ## Service Handler

(defn bytes->bytebuffers
  "Wraps a collection of byte arrays in ByteBuffers."
  [coll]
  (map (fn [^bytes x]
         (ByteBuffer/wrap x)) coll))

(defn bytebuffers->bytes
  "Unwraps a collection of byte arrays from inside Byte Buffers."
  [coll]
  (map (fn [^ByteBuffer x]
         (let [ret (byte-array (.remaining x))]
           (.get x ret)
           ret))
       coll))

(defn try-direct-multi-get
  "Attempts a direct multi-get to the supplied service for each of the
  keys in the supplied `key-seq`."
  [^BisonDB$Iface service domain-name error-suffix key-seq]
  (let [key-set (into #{} (bytes->bytebuffers key-seq))]
    (try (.directMultiGet service domain-name key-set)
         (catch TException e
           (log/error e "Thrift exception on " error-suffix "trying next host")) ;; try next host
         (catch WrongHostException e
           (log/error e "Fatal exception on " error-suffix)
           (throw (TException. "Fatal exception when performing get" e)))
         (catch DomainNotFoundException e
           (log/error e "Could not find domain when executing read on " error-suffix)
           (throw e))
         (catch DomainNotLoadedException e
           (log/error e "Domain not loaded when executing read on " error-suffix)
           (throw e)))))

;; multi-get* recieves a sequence of indexed-keys. Each of these is a
;; map with :index, :key and :host keys. On success, it returns the
;; indexed-keys input with :value keys associated onto every map. On
;; failure it throws an exception, or returns nil.

(defn multi-get*
  [service domain-name database localhost hostname indexed-keys]
  (let [port     (:port database)
        key-seq (map :key indexed-keys)
        suffix   (format "%s:%s/%s" hostname domain-name key-seq)]
    (when-let [results-map (if (= localhost hostname)
                             (try-direct-multi-get service
                                                   domain-name
                                                   suffix
                                                   key-seq)
                             (c/with-bison hostname port remote-service
                               (try-direct-multi-get remote-service
                                                     domain-name
                                                     suffix
                                                     key-seq)))]
      results-map)))

;; TODO: Perfect example of a spot where we could throw a data
;; structure warning up with throw+ if the database isn't loaded.

(defn direct-multiget [database domain-name key-seq]
  (let [domain (db/domain-get database domain-name)
        metrics (db/metrics-get database domain-name)]
    (when (loaded? domain)
      (time! (:direct-get-response-time metrics)
             (into {} (map (fn [key]
                             {(ByteBuffer/wrap key) (thrift/mk-value (dom/kv-get domain key))}) key-seq))))))

;; ## MultiGet

(defn multi-get
  [get-fn database domain-name key-seq]
  (let [indexed-keys (-> (db/domain-get database domain-name)
                         (dom/index-keys key-seq))]
    (if-let [bad-key (some (comp empty? :hosts) indexed-keys)]
      (throw (thrift/hosts-down-ex (:all-hosts bad-key)))
      (let [host-map (group-by :hosts indexed-keys)
            promises (map
                      (fn [[hosts indexed-keys]]
                        (let [p (promise)]
                          (u/do-pmap
                           (fn [host]
                             (when-not (realized? p)
                               (deliver p (get-fn host indexed-keys)))) hosts)
                          p)) host-map)
            metrics (db/metrics-get database domain-name)]
        (time! (:multi-get-response-time metrics) (into {} (map deref promises)))))))

(defn kv-get-fn
  [service domain-name database]
  (partial multi-get*
           service
           domain-name
           database
           (u/local-hostname)))

;; TODO: Catch errors if we're not dealing specifically with a byte array.

(defn kv-service [database]
  (reify BisonDB$Iface
    (directMultiGet [_ domain-name key-set]
      (thrift/assert-domain database domain-name)
      (let [key-seq (bytebuffers->bytes key-set)]
        (try (if-let [results-map (time! direct-get-response-time (direct-multiget database domain-name key-seq))]
               results-map
               (throw (thrift/domain-not-loaded-ex)))
             (catch RuntimeException _
               (throw (thrift/wrong-host-ex))))))

    (multiGet [this domain-name key-set]
      (thrift/assert-domain database domain-name)
      (let [get-fn (kv-get-fn this domain-name database)]
        (time! multi-get-response-time
               (multi-get get-fn
                          database
                          domain-name
                          (bytebuffers->bytes key-set)))))

    (get [this domain-name key]
      (thrift/assert-domain database domain-name)
      (let [get-fn (kv-get-fn this domain-name database)
            ret (byte-array (.remaining key))]
        (mark! get-requests)
        (.get key ret)
        (first (vals (multi-get get-fn database domain-name [ret])))))

    (getDomainStatus [_ domain-name]
      "Returns the thrift status of the supplied domain-name."
      (thrift/assert-domain database domain-name)
      (-> (db/domain-get database domain-name)
          (status/get-status)
          (thrift/to-thrift)))

    (getDomains [_]
      "Returns a sequence of all domain names being served."
      (db/domain-names database))

    (getStatus [_]
      "Returns a map of domain-name->status for each domain."
      (thrift/bison-status
       (u/update-vals (db/domain->status database)
                      (fn [_ status] (thrift/to-thrift status)))))

    (isFullyLoaded [_]
      "Are all domains loaded properly?"
      (db/fully-loaded? database))

    (isUpdating [_]
      "Is some domain currently updating?"
      (db/some-loading? database))

    (update [_ domain-name]
      "If an update is available, updates the named domain and
         hotswaps the new version."
      (thrift/assert-domain database domain-name)
      (u/with-ret true
        (db/attempt-update! database domain-name)))

    (updateAll [_]
      "If an update is available on any domain, updates the domain's
         shards from its remote store and hotswaps in the new versions."
      (u/with-ret true
        (db/update-all! database)))

    (getCount [_ domain-name]
      "Returns the total count of KeyValDocuments in the supplied domain-name."
      (thrift/assert-domain database domain-name)
      (-> (db/domain-get database domain-name)
          (dom/kv-count)))

    (getDomainMetaData [_ domain-name]
      (thrift/assert-domain database domain-name)
      (-> (db/domain-get database domain-name)
          (metadata/get-metadata)))
    
    (getMetaData [_]
      "Returns a map of domain-name->metadata for each domain."
      (thrift/bison-metadata
       (db/domain->metadata database)))))

;; # Main Access
;;
;; This namespace is the main access point to the bdb
;; code. bisondb.keyval/-main Boots up the BisonDB service and
;; an updater process that watches all domains and trigger an atomic
;; update in the background when some new version appears.
;;
;; TODO: Booting needs a little work; I'll do this along with the
;; deploy.

(defn -main
  "Main booting function for all of BDB. Pass in:

  `global-config-hdfs-path`: the hdfs path of `global-config.clj`

  `local-config-path`: the path to `local-config.clj` on this machine."
  [global-config-hdfs-path local-config-path]
  (let [local-config   (conf/read-local-config  local-config-path)
        global-config  (conf/read-global-config global-config-hdfs-path
                                                local-config)
        conf-map (merge global-config local-config)
        database (db/build-database conf-map)]
    (when (:ui-port conf-map)
      (db/launch-ui! conf-map))
    (doto database
      (db/prepare)
      (db/launch-updater! (:update-interval-s conf-map)))
    (when-let [graphite-conf (:graphite-reporter conf-map)]
      (log/info "Metrics graphite reporter started.")
      (report-to-graphite (:host graphite-conf) (:port graphite-conf)))
    (when-let [ganglia-conf (:ganglia-reporter conf-map)]
      (log/info "Metrics ganglia reporter started.")
      (report-to-ganglia (:host ganglia-conf) (:port ganglia-conf)))
    (when-let [metrics-reporting-interval-s (:metrics-reporting-interval-s conf-map)]
      (log/info "Metrics console reporter started.")
      (report-to-console metrics-reporting-interval-s))
    (thrift/launch-server! kv-processor
                           (kv-service database)
                           (:port conf-map))))

;; For debugging in the a repl

(defn debug-database
  "Main booting function for all of BDB. Pass in:

  `global-config-hdfs-path`: the hdfs path of `global-config.clj`

  `local-config-path`: the path to `local-config.clj` on this machine."
  [global-config-hdfs-path local-config-path]
  (let [local-config   (conf/read-local-config  local-config-path)
        global-config  (conf/read-global-config global-config-hdfs-path
                                                local-config)
        conf-map (merge global-config local-config)
        database (db/build-database conf-map)]
    (doto database
      (db/prepare)
      (db/launch-updater! (:update-interval-s conf-map)))))
