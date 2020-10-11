(def ROOT-DIR (subs *file* 0 (- (count *file*) (count "project.clj"))))
(def VERSION (-> ROOT-DIR (str "/../VERSION") slurp))

;; allow insecure downloads
(require 'cemerick.pomegranate.aether)
(cemerick.pomegranate.aether/register-wagon-factory!
  "http" #(org.apache.maven.wagon.providers.http.HttpWagon.))

(defproject bisondb/bisondb-berkeleydbje VERSION
  :min-lein-version "2.0.0"
  :java-source-paths ["src/jvm"]
  :javac-options ["-source" "1.6" "-target" "1.6"]
  :repositories {"oracle" "http://download.oracle.com/maven"}
  :dependencies [[bisondb/bisondb-core ~VERSION]
                 [com.sleepycat/je "5.0.58"]]
  :profiles {:dev
             {:dependencies
              [[midje "1.6.3"]]
              :plugins [[lein-midje "3.1.3"]]}})
