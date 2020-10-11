(def VERSION (slurp "VERSION"))
(def MODULES (-> "MODULES" slurp (.split "\n")))
(def DEPENDENCIES (for [m MODULES] [(symbol (str "bisondb/" m)) VERSION]))

;; allow insecure downloads
(require 'cemerick.pomegranate.aether)
(cemerick.pomegranate.aether/register-wagon-factory!
  "http" #(org.apache.maven.wagon.providers.http.HttpWagon.))

(eval `(defproject bisondb/bisondb ~VERSION
         :description "Distributed database specialized in exporting key/value data from Hadoop"
         :url "https://github.com/jkrentz/bisondb"
         :license {:name "BSD-3-Clause"
                   :url "https://opensource.org/licenses/BSD-3-Clause"}
         :min-lein-version "2.0.0"
         :dependencies [~@DEPENDENCIES]
         :plugins [[~'lein-sub "0.3.0"]]
         :sub [~@MODULES]
         :profiles {:dev {:dependencies [[~'midje "1.6.3"]]
                          :plugins [[~'lein-midje "3.1.3"]]}}))
