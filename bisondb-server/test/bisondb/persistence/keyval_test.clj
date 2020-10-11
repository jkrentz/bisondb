(ns bisondb.persistence.keyval-test
  (:use midje.sweet
        bisondb.test.common
        bisondb.test.keyval)
  (:require [hadoop-util.test :as t])
  (:import [bisondb.document KeyValDocument]
           [bisondb.persistence JavaBerkDB LevelDB]))

(defn test-get-put [coordinator]
  (t/with-local-tmp [_ tmp-path]
    (.createPersistence coordinator tmp-path {})
    (with-open [db (.openPersistenceForAppend coordinator tmp-path {})]
      (fact (bdb-get db "a") => nil)
      (bdb-put db "a" "1")
      (bdb-put db "b" "2")
      (facts
        (bdb-get db "a") => "1"
        (bdb-get db "b") => "2"
        (bdb-get db "c") => nil))

    (with-open [db (.openPersistenceForRead coordinator tmp-path {})]
      (facts
        (bdb-get db "a") => "1"
        (bdb-get db "b") => "2"
        (bdb-get db "c") => nil))

    (with-open [db (.openPersistenceForAppend coordinator tmp-path {})]
      (fact (bdb-get db "a") => "1")
      (bdb-put db "a" "11")
      (fact (bdb-get db "a") => "11"))))

(defn is-db-pairs?
  "Returns true if the persistence housed by the supplied coordinator
  contains the supplied pairs, false otherwise."
  [coordinator path & pairs]
  (with-open [db (.openPersistenceForRead coordinator path {})]
    (fact (get-all db) => (just pairs))))

;; ## DomainStore Level Testing
(defn test-iterate [coord]
  (t/with-local-tmp [_ tmp-path]
    (create-pairs coord tmp-path ["a" "1"])
    (is-db-pairs? coord tmp-path ["a" "1"])
    (append-pairs coord tmp-path ["c" "3"] ["b" "4"])
    (is-db-pairs? coord tmp-path ["a" "1"] ["b" "4"] ["c" "3"])
    (append-pairs coord tmp-path ["a" "4"] ["d" "5"])
    (is-db-pairs? coord tmp-path ["a" "4"] ["b" "4"] ["c" "3"] ["d" "5"])))

;; ## Coordinator Testing
;;
;; These tests use the above functions to run Coordinators through a
;; test battery. The tests rely on interfaces, making them appropriate
;; for any defined Coordinator.

(future-facts
 "Tests that BerkeleyDB is able to put and get tuples.
  TODO: move to persistence module."
  (test-get-put (JavaBerkDB.))
  (test-iterate (JavaBerkDB.)))

(future-facts
 "Tests that LevelDB is able to put and get tuples.
  TODO: move to persistence module."
  (test-get-put (LevelDB.))
  (test-iterate (LevelDB.)))
