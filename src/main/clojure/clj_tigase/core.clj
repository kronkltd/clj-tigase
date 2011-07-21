(ns clj-tigase.core
  (:use (clj-tigase element packet)
        [clojure.string :only (trim)])
  (:import javax.xml.namespace.QName
           tigase.conf.ConfigurationException
           tigase.conf.ConfiguratorAbstract
           tigase.server.Packet
           tigase.server.MessageRouter
           tigase.server.XMPPServer
           tigase.xml.Element
           tigase.xmpp.JID))

(defonce ^:dynamic *message-router* (ref nil))
(defonce ^:dynamic *configurator* (ref nil))
(defonce ^:dynamic *configurator-prop-key* "tigase-configurator")
(defonce ^:dynamic *default-configurator* "tigase.conf.Configurator")
(defonce ^:dynamic *name* "Tigase")
(defonce ^:dynamic *server-name* "message-router")

(defn make-packet
  [{:keys [to from ^String body type id] :as packet-map}]
  (let [element-name (condp = type
                         :result "iq"
                         :set "iq"
                         :get "iq"
                         :chat "message"
                         :headline "message")
        ^Element element (make-element
                          [element-name {"id" id
                                         "type" (if type (name type) "")
                                         "to" to
                                         "from" from}])]
    (if body (.addChild element body))
    (Packet/packetInstance element from to)))

(defn set-packet
  [request body]
  {:body body
   :from (:to request)
   :to (:from request)
   :id (:id request)
   :type :set})

(defn result-packet
  [request body]
  (merge
   (if body {:body (make-element body)})
   {:from (:to request)
    :to (:from request)
    :id (:id request)
    :type :result}))

(defn respond-with
  "given an item element, returns a packet"
  [request ^Element item]
  (let [^Packet packet (:packet request)]
    (.okResult packet item 0)))

(defn make-jid
  "Creates a JID in a variety of ways"
  ([{:keys [username domain]}]
     (make-jid username domain))
  ([user domain]
     (make-jid user domain ""))
  ([user domain resource]
     (JID/jidInstance user domain resource)))

(defn deliver-packet!
  [^Packet packet]
  (try
    (.initVars packet)
    (.processPacket ^MessageRouter @*message-router* packet)
    (catch NullPointerException e
      #_(error "Router not started: " e)
      #_(stacktrace/print-stack-trace e)
      packet)))

(defn ^MessageRouter get-router
  [args ^ConfiguratorAbstract config]
  (let [mr-class-name (.getMessageRouterClassName config)]
    (.newInstance (Class/forName mr-class-name))))

(defmacro with-router
  [router & body]
  `(binding [jiksnu.xmpp.router/*message-router* ~router]
     ~@body))

(defn process!
  [^Packet packet]
  (.processPacket ^MessageRouter *message-router* packet))

(defn get-config
  [initial-config tigase-options]
  (ConfiguratorAbstract/loadLogManagerConfig initial-config)
  (let [config-class-name (System/getProperty
                           *configurator-prop-key*
                           *default-configurator*)
        ^ConfiguratorAbstract config
        (.newInstance (Class/forName config-class-name))]
    (.init config tigase-options)
    (.setName config "basic-conf")
    (dosync
     (ref-set *configurator* config))
    config))

(defn start-router!
  [tigase-options config]
  (dosync
   (ref-set
    *message-router*
    (doto (get-router tigase-options config)
      (.setName *server-name*)
      (.setConfig config)
      .start))))
