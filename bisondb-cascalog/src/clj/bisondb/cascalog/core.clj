(ns bisondb.cascalog.core
  (:use [bisondb.cascalog conf]
        [cascalog.api :only (cascalog-tap)])
  (:import [bisondb Utils]
           [bisondb.cascading BisonDBTap BisonDBTap$TapMode]
           [org.apache.hadoop.conf Configuration]))

(defn bison-tap
  [root-path & {:keys [spec sink-fn] :as args}]
  (let [args (convert-args args)
        spec (when spec
               (convert-domain-spec spec))
        source-tap (BisonDBTap. root-path spec args BisonDBTap$TapMode/SOURCE)
        sink-tap (BisonDBTap. root-path spec args BisonDBTap$TapMode/SINK)]
    (cascalog-tap source-tap
                  (if sink-fn
                    (fn [tuple-src]
                      [sink-tap (sink-fn sink-tap tuple-src)])
                    sink-tap))))
