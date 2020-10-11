(ns bisondb.common.spec-test
  (:use midje.sweet
        [bisondb.test.common :only (berkeley-spec)])
  (:import [bisondb DomainSpec Utils]
           [bisondb.document KeyValDocument]))

;; ## DomainSpec Testing

(fact
  "DomainSpec equality is value-based, not instance-based."
  (DomainSpec. "bisondb.persistence.JavaBerkDB"
               "bisondb.partition.HashModScheme"
               2)
  => (DomainSpec. (bisondb.persistence.JavaBerkDB.)
                  (bisondb.partition.HashModScheme.)
                  2))

;; ## DomainSpec functionality

(fact
  "Spec should only allow positive numbers for the shard-count."
  (berkeley-spec 10)  => truthy
  (berkeley-spec 0)   => (throws AssertionError)
  (berkeley-spec -10) => (throws AssertionError))
