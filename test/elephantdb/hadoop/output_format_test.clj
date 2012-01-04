(ns elephantdb.hadoop.output-format-test
  (:use midje.sweet
        elephantdb.common.testing
        elephantdb.keyval.testing)
  (:require [hadoop-util.core :as h]
            [jackknife.core :as u])
  (:import [elephantdb DomainSpec Utils]
           [elephantdb.hadoop ElephantOutputFormat
            ElephantOutputFormat$Args]
           [org.apache.hadoop.io IntWritable BytesWritable]
           [elephantdb.document KeyValDocument]
           [elephantdb.index Indexer StringAppendIndexer]
           [elephantdb.persistence JavaBerkDB]
           [elephantdb.store VersionedStore]))

(def test-spec
  (mk-test-spec 2))

(defn write-data
  [writer data]
  (let [serializer (Utils/makeSerializer test-spec)]
    (u/dofor [[s records] data
              [k v]      records]
             (.write writer
                     (IntWritable. s)
                     (BytesWritable.
                      (.serialize serializer (KeyValDocument. k v)))))))

(defn check-shards
  [fs lfs output-dir local-tmp expected]
  (.mkdirs lfs (h/path local-tmp))
  (u/dofor [[s records] expected]
           (let [local-shard-path (h/str-path local-tmp s)
                 persistence (do (.copyToLocalFile fs
                                                   (h/path output-dir (str s))
                                                   (h/path local-shard-path))
                                 (.openPersistenceForRead (.getCoordinator test-spec)
                                                          local-shard-path
                                                          {}))]
             (u/dofor [[k v] records]
                      (fact (.get persistence k) => v))
             (u/dofor [[_ non-records] (dissoc expected s)
                       [k _] non-records]
                      (fact (.get persistence k) => nil?)))))

(deftest test-output-format
  (with-fs-tmp [fs output-dir]
    (with-local-tmp [lfs etmp tmp2]
      (let [data {0 {"0a" "00" "0b" "01"} 4 {"4a" "40"}}
            writer  (mk-elephant-writer 10 (JavaBerkDB.) output-dir etmp)]
        (write-data writer data)
        (.close writer nil)
        (fact (count (.listStatus fs (h/path output-dir))) => 2)
        (check-shards fs lfs output-dir tmp2  data)))))

(deftest test-incremental
  (with-fs-tmp [fs dir1 dir2]
    (with-local-tmp [lfs ltmp1 ltmp2]
      (mk-presharded-domain fs dir1 (JavaBerkDB.)
                            {0 [["a" "1"]]
                             1 [["b" "2"]
                                ["c" "3"]]})
      (let [data {0 {"a" "2" "d" "4"} 1 {"c" "4" "e" "4"} 2 {"x" "x"}}
            writer (mk-elephant-writer 3 (JavaBerkDB.) dir2 ltmp1
                                       :indexer (StringAppendIndexer.)
                                       :update-dir (.mostRecentVersionPath
                                                    (VersionedStore. dir1)))]
        (write-data writer data)
        (.close writer nil)
        (check-shards fs lfs dir2 ltmp2
                      {0 {"a" "12" "d" "4"}
                       1 {"b" "2" "c" "34" "e" "4"}
                       2 {"x" "x"}})))))

(future-fact "test errors.")
