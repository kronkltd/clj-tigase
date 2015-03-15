(defproject clj-tigase "0.2.0-SNAPSHOT"
  :description "Clojure library for working with Tigase XMPP Server"
  :url "http://github.com/duck1123/clj-tigase"
  :min-lein-version "2.0.0"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repositories {"tigase-snapshots" "http://maven.tigase.org/"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [tigase/tigase-server "7.0.0"]]
  :profiles {:dev
             {:dependencies
              [[midje "1.7.0-SNAPSHOT"]]}}
  :plugins [[codox "0.8.10"]
            [lein-midje "3.1.3"]])
