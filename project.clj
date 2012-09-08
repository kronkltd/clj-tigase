(defproject clj-tigase "0.1.0-SNAPSHOT"
  :description "Clojure library for working with Tigase XMPP Server"
  :url "http://github.com/duck1123/clj-tigase"
  :repositories {"tigase-snapshots" "http://maven.tigase.org/"}
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [tigase/tigase-server "5.1.0-SNAPSHOT"]]
  :dev-dependencies [[midje "1.4.0"]
                     [lein-midje "1.0.10"]]
  :warn-on-reflection true
  :jvm-opts ["-server"])
