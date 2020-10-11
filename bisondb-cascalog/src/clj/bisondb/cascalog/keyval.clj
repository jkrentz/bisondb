(ns bisondb.cascalog.keyval
  (:use cascalog.api
        bisondb.cascalog.conf)
  (:require [bisondb.cascalog.core :as core])
  (:import [cascalog.ops IdentityBuffer]
           [org.apache.hadoop.conf Configuration]
           [bisondb Utils]
           [bisondb.partition ShardingScheme]
           [org.apache.hadoop.io BytesWritable]))

(defn- test-array
  [t]
  (let [check (type (t []))]
    (fn [arg] (instance? check arg))))

(def ^{:private true} byte-array?
  (test-array byte-array))

(defn shard
  "Returns the shard to which the supplied shard-key should be
  routed."
  [^ShardingScheme scheme shard-count]
  (mapfn
   [^bytes shard-key]
   {:pre [(byte-array? shard-key)]}
   (.shardIndex scheme shard-key shard-count)))

(defmapfn mk-sortable-key [^bytes shard-key]
  {:pre [(byte-array? shard-key)]}
  (BytesWritable. shard-key))

(defn bison<-
  [bison-tap kv-src]
  (let [spec        (.getSpec bison-tap)
        scheme      (.getShardScheme spec)
        shard-count (.getNumShards spec)]
    (<- [!shard !key !value]
        (kv-src !keyraw !valueraw)
        ((shard scheme shard-count) !keyraw :> !shard)
        (mk-sortable-key !keyraw :> !sort-key)
        (:sort !sort-key)
        ((IdentityBuffer.) !keyraw !valueraw :> !key !value))))

(defn keyval-tap
  "Returns a tap that can be used to source and sink key-value pairs
  to BisonDB."
  [root-path & {:as args}]
  (let [args (merge {:source-fields ["key" "value"]}
                    args
                    {:sink-fn bison<-})]
    (apply core/bison-tap
           root-path
           (apply concat args))))

(defn reshard!
  "Accepts two target paths and a new shard count and re-shards the
  domain at source-dir into target-dir. (To re-shard a domain into
  itself, pass the same path in for source and target.)"
  [source-dir target-dir shard-count]
  (let [fs (Utils/getFS source-dir (Configuration.))
        spec (read-domain-spec fs source-dir)
        new-spec (assoc spec :num-shards shard-count)]
    (?- (keyval-tap target-dir :spec new-spec)
        (keyval-tap source-dir))))
