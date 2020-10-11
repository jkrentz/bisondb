(def ROOT-DIR (subs *file* 0 (- (count *file*) (count "project.clj"))))
(def VERSION (-> ROOT-DIR (str "/../VERSION") slurp))

(defproject bisondb/bisondb-cascading VERSION
  :min-lein-version "2.0.0"
  :java-source-paths ["src/jvm"]
  :javac-options ["-source" "1.6" "-target" "1.6"]
  :jvm-opts ["-server" "-Xmx768m"]
  :repositories {"conjars" "http://conjars.org/repo"}
  :dependencies [[bisondb/bisondb-core ~VERSION]
                 [cascading/cascading-hadoop "2.5.3"
                  :exclusions [org.codehaus.janino/janino
                               org.apache.hadoop/hadoop-core]]]
  :profiles {:provided
             {:dependencies [[org.apache.hadoop/hadoop-core "1.2.1"]]}
             :dev
             {:dependencies
              [[bisondb/bisondb-berkeleydbje ~VERSION]
               [hadoop-util "0.3.0"]
               [jackknife "0.1.6"]
               [midje "1.6.3"]]
              :plugins [[lein-midje "3.1.3"]]}})
