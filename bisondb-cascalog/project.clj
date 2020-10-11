(def ROOT-DIR (subs *file* 0 (- (count *file*) (count "project.clj"))))
(def VERSION (-> ROOT-DIR (str "/../VERSION") slurp))
(def CASCALOG-VERSION "2.1.0")

(defproject bisondb/bisondb-cascalog VERSION
  :min-lein-version "2.0.0"
  :description "BisonDB Integration for Cascalog."
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]
  :javac-options ["-source" "1.6" "-target" "1.6"]
  :jvm-opts ["-server" "-Xmx768m"]
  :exclusions [org.clojure/clojure]
  :dependencies [[bisondb/bisondb-cascading ~VERSION]]
  :profiles {:provided {:dependencies [[org.clojure/clojure "1.5.1"]
                                       [cascalog/cascalog-core ~CASCALOG-VERSION]]}
             :dev {:dependencies
                   [[midje "1.6.3"]
                    [bisondb/bisondb-berkeleydbje ~VERSION]
                    [org.apache.hadoop/hadoop-core "1.2.1"]
                    [cascalog/midje-cascalog ~CASCALOG-VERSION]]
                   :plugins [[lein-midje "3.1.3"]]}})
