(ns bisondb.persistence.shardset-test
  (:use midje.sweet
        bisondb.test.common)
  (:require [hadoop-util.test :as t])
  (:import [bisondb DomainSpec]
           [bisondb.persistence ShardSet ShardSetImpl]))

(defn mk-spec
  "Returns a spec configured for the supplied number of shards."
  [shard-count]
  (DomainSpec. (bisondb.persistence.JavaBerkDB.)
               (bisondb.partition.HashModScheme.)
               shard-count))

(defn shard-set [path spec]
  (ShardSetImpl. path spec))

(defn shard-count [num-shards]
  (t/with-fs-tmp [_ tmp]
    (let [set (shard-set tmp (mk-spec num-shards))]
      (.getNumShards set))))

(fact
  "ShardSet shouldn't allow negative shard counts."
  (shard-count 10)  => 10
  (shard-count 0)   => (throws AssertionError)
  (shard-count -10) => (throws AssertionError))

(t/with-fs-tmp [_ tmp]
  (let [set-impl (shard-set tmp (mk-spec 5))]
    (fact "ShardSetImpl should enforce the shard-count with assertions."
      (.assertValidShard set-impl 0) => nil
      (.assertValidShard set-impl 5) => (throws AssertionError))

    (fact "ShardSetImpl shouldn't create out-of-bounds shards."
      (.createShard set-impl 0)  => truthy
      (.createShard set-impl 4)  => truthy
      (.createShard set-impl -1) => (throws AssertionError)
      (.createShard set-impl 5)  => (throws AssertionError))))




