(ns clj-tigase.core
   (:use clj-tigase.element
         clj-tigase.packet
         [clojure.string :only [trim]])
  (:import javax.xml.namespace.QName
           tigase.conf.ConfigurationException
           tigase.conf.ConfiguratorAbstract
           tigase.server.Packet
           tigase.server.MessageRouter
           tigase.server.XMPPServer
           tigase.xml.Element
           tigase.xmpp.BareJID
           tigase.xmpp.JID))

(defonce ^:dynamic *message-router* (ref nil))
(defonce ^:dynamic *configurator* (ref nil))
(defonce ^:dynamic *configurator-prop-key* "tigase-configurator")
(defonce ^:dynamic *default-configurator* "tigase.conf.Configurator")
(defonce ^:dynamic *name* "Tigase")
(defonce ^:dynamic *server-name* "message-router")

(def packet-types
  {:result "iq"
   :set "iq"
   :get "iq"
   :chat "message"
   :headline "message"})

(defn make-packet
  [{:keys [to from ^String body type id] :as packet-map}]
  (let [element-name (get packet-types type "iq")
        ^Element element (make-element
                          [element-name {"id" id
                                         "type" (if type (name type) "")
                                         "to" to
                                         "from" from}])]
    (when body (.addChild element body))
    (Packet/packetInstance element from to)))

(defn set-packet
  [request body]
  (let [{:keys [to from id]} request]
    {:body body, :from to, :to from, :id id, :type :set}))

(defn result-packet
  [request body]
  (let [{:keys [to from id]} request]
    (merge
     (if body {:body (make-element body)})
     {:from to :to from :id id :type :result})))

(defn respond-with
  "given an item element, returns a packet"
  [request ^Element item]
  (let [^Packet packet (:packet request)]
    (.okResult packet item 0)))

(defn get-id
  "Return the local part of a jid"
  [^JID jid]
  (.getLocalpart jid))

(defn get-domain
  "Refurn the domain part of a jid"
  [user]
  (.getDomain user))

(defn bare-jid
  "Create a bare jid instance"
  [local domain]
  (BareJID/bareJIDInstance local domain))

(defn make-jid
  "Creates a JID in a variety of ways"
  ([{:keys [username domain]}]
     (make-jid username domain))
  ([user domain]
     (make-jid user domain ""))
  ([user domain resource]
     (JID/jidInstance user domain resource)))

(defn deliver-packet!
  "Send pacet to the message router to be processed"
  [^Packet packet]
  (try
    (.initVars packet)
    (.processPacket ^MessageRouter @*message-router* packet)
    (catch NullPointerException e
      #_(error "Router not started: " e)
      #_(stacktrace/print-stack-trace e)
      packet)))

(defn ^MessageRouter get-router
  "Create a message router from the configuration"
  [args ^ConfiguratorAbstract config]
  (let [mr-class-name (.getMessageRouterClassName config)]
    (.newInstance (Class/forName mr-class-name))))

(defmacro with-router
  "Execute body with message router bound"
  [router & body]
  `(binding [jiksnu.xmpp.router/*message-router* ~router]
     ~@body))

(defn process!
  [^Packet packet]
  (.processPacket ^MessageRouter *message-router* packet))

(defn get-config
  "Get configurator from configuration information"
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
  "Start a message router using the configuration information"
  [tigase-options config]
  (dosync
   (ref-set
    *message-router*
    (doto (get-router tigase-options config)
      (.setName *server-name*)
      (.setConfig config)
      .start))))
