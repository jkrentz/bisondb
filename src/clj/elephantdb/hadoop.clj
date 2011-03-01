(ns elephantdb.hadoop
  (:import [org.apache.hadoop.fs FileSystem Path]
           [org.apache.hadoop.conf Configuration])
  (:import [java.io File FileNotFoundException FileOutputStream BufferedOutputStream])
  (:use [elephantdb log]))

(defmulti conf-set (fn [obj] (class (:value obj))))

(defmethod conf-set String [{key :key value :value conf :conf}]
  (.set conf key value))

(defmethod conf-set Integer [{key :key value :value conf :conf}]
  (.setInt conf key value))

(defmethod conf-set Float [{key :key value :value conf :conf}]
  (.setFloat conf key value))

(defn path
  ([str-or-path]
    (if (instance? Path str-or-path) str-or-path (Path. str-or-path)))
  ([parent child] (Path. parent child)))

(defn str-path
  ([part1]
    part1)
  ([part1 part2 & components]
    (apply str-path (str (path part1 (str part2))) components)))

(defn configuration [conf-map]
  (let [ret (Configuration.)]
    (doall
      (for [config conf-map]
        (conf-set {:key (first config) :value (last config) :conf ret})))
    ret))

(defn filesystem
  ([] (FileSystem/get (Configuration.)))
  ([conf-map]
    (FileSystem/get (configuration conf-map))))

(defn mkdirs [fs path]
  (.mkdirs fs (Path. path)))

(defn delete
  ([fs path] (delete fs path false))
  ([fs path rec]
  (.delete fs (Path. path) rec)))

(defn clear-dir [fs path]
  (delete fs path true)
  (mkdirs fs path))

(defn local-filesystem [] (FileSystem/getLocal (Configuration.)))

;; holds the total amount of bytes of data downloaded since the last
;; reset to 0
;; is used to determine the current download rate (kb/s)
(def downloaded-kb (atom 0))
;; indicator for downloaders if they can download or not.
;; gets set by supervising thread, defaults to true (e.g. on startup)
(def do-download (atom true))

;; gets incremented when a domain has finished loading
(def finished-loaders (atom 0))

(declare copy-local*)

(defn- copy-file-local [#^FileSystem fs #^Path path #^String target-local-path #^bytes buffer]
  (with-open [is (.open fs path)
              os (BufferedOutputStream.
                  (FileOutputStream. target-local-path))]
    (loop []
      (if @do-download
        (let [amt (.read is buffer)]
          (when (> amt 0)
            (.write os buffer 0 amt)
            (swap! downloaded-kb + (int (/ amt 1024))) ;; increment downloaded-kb
            (recur)))
        (recur))) ;; keep looping
    ))

(defn copy-dir-local [#^FileSystem fs #^Path path #^String target-local-path #^bytes buffer]
  (.mkdir (File. target-local-path))
  (let [contents (seq (.listStatus fs path))]
    (doseq [c contents]
      (let [subpath (.getPath c)]
        (copy-local* fs subpath (str-path target-local-path (.getName subpath)) buffer)
        ))))

(defn- copy-local* [#^FileSystem fs #^Path path #^String target-local-path #^bytes buffer]
  (let [status (.getFileStatus fs path)]
    (cond (.isDir status) (copy-dir-local fs path target-local-path buffer)
          true (copy-file-local fs path target-local-path buffer)
          )))

(defn copy-local [#^FileSystem fs #^String spath #^String local-path]
  (let [target-file (File. local-path)
        source-name (.getName (Path. spath))
        buffer (byte-array (* 1024 15))
        dest-path (cond
                   (not (.exists target-file)) local-path
                   (.isFile target-file) (throw
                                          (IllegalArgumentException.
                                           (str "File exists " local-path)))
                   (.isDirectory target-file) (str-path local-path source-name)
                   true (throw
                         (IllegalArgumentException.
                          (str "Unknown error, local file is neither file nor dir " local-path))))]
    (when-not (.exists fs (path spath))
      (throw
       (FileNotFoundException.
        (str "Could not find on remote " spath))))
    (copy-local* fs (path spath) dest-path buffer)
    ))
