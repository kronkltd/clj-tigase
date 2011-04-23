(ns clj-tigase.core
  (:use [clojure.string :only (trim)])
  (:import javax.xml.namespace.QName
           tigase.conf.ConfigurationException
           tigase.conf.ConfiguratorAbstract
           tigase.server.Packet
           tigase.server.XMPPServer
           tigase.xml.Element
           tigase.xmpp.JID))

(def #^:dynamic *message-router* (ref nil))

(def #^:dynamic *configurator-prop-key* "tigase-configurator")
(defonce #^:dynamic *default-configurator* "tigase.conf.Configurator")

(def #^:dynamic *name* "Tigase")
(def #^:dynamic *server-name* "message-router")

(declare to-tigase-element)

(defn packet?
  "Returns if the element is a packet"
  [element]
  (instance? Packet element))

(defn element?
  "Returns if the argument is an element"
  [arg]
  (instance? Element arg))

(defn parse-qname
  [^QName qname]
  {:name (.getLocalPart qname)
   :prefix (.getPrefix qname)})

(defn ns-prefix
  [k]
  (apply str
         "xmlns"
         (if (not= k "")
           (list ":" k))))

(defn element-name
  [name prefix]
  (str (if (not= prefix "")
         (str prefix ":"))
       name))

(defn make-element-qname
  [{:keys [name prefix]}]
  (Element. (element-name name prefix)))

(defn get-qname
  "Returns a map representing the QName of the given element"
  [element]
  (parse-qname (.getQName element)))

(defn assign-namespace
  [^Element element
   namespace-map
   [k v]]
  (if (not= (get namespace-map k) v)
    (do (.addAttribute
         element (ns-prefix k) v)
        [k v])))

(declare make-element)

(defn process-child
  "adds content of the appropriate type to the element"
  [^Element element item]
  #_(println "item: " item)
  (if (element? item)
    (.addChild element item)
    (if (map? item)
      (.addChild element (to-tigase-element item))
      (if (vector? item)
        (if (seq item)
          (.addChild element (apply make-element item)))
        (if (string? item)
          (.setCData element (trim item))
          (if (coll? item)
            (doseq [i item]
              (process-child element i))))))))

(defn make-element
  "Create a tigase element"
  ([spec]
     (apply make-element spec))
  ([name attrs]
     (make-element name attrs nil))
  ([name attrs & children]
     (let [element (Element. name)]
       (doseq [[attr val] attrs]
         (.addAttribute element attr (str val)))
       (doseq [child children]
         (process-child element child))
       element)))

(defn to-tigase-element
  "turns a map into a tigase element"
  [{:keys [tag attrs content]}]
  (let [attribute-names (into-array String (map name (keys attrs)))
        attribute-values (into-array String (vals attrs))
        tag-name (name tag)
        element (Element. tag-name attribute-names attribute-values)]
    (doseq [item content]
      (process-child element item))
    element))

(defn children
  "returns the child elements of the given element"
  ([^Element element]
     (if element
       (seq (.getChildren element))))
  ([^Packet packet path]
     (if packet
       (seq (.getElemChildren packet path)))))

(defn merge-namespaces
  [^Element element
   namespace-map
   namespaces]
  (merge namespace-map
         (into {}
               (map
                (partial assign-namespace element namespace-map)
                namespaces))))

;; (defn add-children
;;   [^Element element abdera-element bound-namespaces]
;;   (doseq [child-element (.getElements abdera-element)]
;;     (.addChild element
;;                (abdera-to-tigase-element
;;                 child-element bound-namespaces))))

(defn add-attributes
  [^Element element abdera-element]
  (doseq [attribute (.getAttributes abdera-element)]
    (let [value (.getAttributeValue abdera-element attribute)]
      (.addAttribute element (.getLocalPart attribute) value))))

(defn make-packet
  [{:keys [to from body type id] :as packet-map}]
  (let [element-name (condp = type
                         :result "iq"
                         :set "iq"
                         :get "iq"
                         :chat "message"
                         :headline "message")
        element (make-element
                 [element-name {"id" id
                                 "type" (if type (name type) "")
                                 "to" to
                                 "from" from}])]
    (if body (.addChild element body))
    (Packet/packetInstance element from to)))

(defn iq-elements
  [^Packet packet]
  (children packet "/iq"))

(defn pubsub-items
  "Returns a seq of pubsub elements contained in a packet"
  [^Packet packet]
  (children packet "/iq/pubsub"))

(defn bare-recipient?
  [^Packet packet]
  (if packet
    (let [recipient-jid (.getStanzaTo packet)]
     (= recipient-jid (.copyWithoutResource recipient-jid)))))

(defn get-items
  [^Packet packet]
  (if-let [node (first (pubsub-items packet))]
    (children node)))

 (defn pubsub-element?
  [^Element element]
  (and element
       (= (.getName element) "pubsub")))

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
  (.okResult (:packet request) item 0))

(defn make-jid
  ([user]
     (make-jid (:username user) (:domain user)))
  ([user domain]
     (make-jid user domain ""))
  ([user domain resource]
     (JID/jidInstance user domain resource)))

(defn node-value
  [#^Element element]
  (.getAttribute element "node"))

(defn make-request
  [^Packet packet]
  (let [type (keyword (str (.getType packet)))
        to (.getStanzaTo packet)
        from (.getStanzaFrom packet)
                payload  (first (iq-elements packet))
        pubsub? (pubsub-element? payload)
        child-node (first (children payload))
        node (and child-node (node-value child-node))
        name (if pubsub?
               (if child-node (.getName child-node))
               (if payload (.getName payload)))]
    {:to to
     :from from
     :pubsub pubsub?
     :payload payload
     :id (.getAttribute packet "id")
     :name name
     :node node
     :ns (if payload (.getXMLNS payload))
     :packet packet
     :request-method type
     :method type
     :items (get-items packet)}))

(defn deliver-packet!
  [^Packet packet]
  (try
    (.initVars packet)
    (.processPacket @*message-router* packet)
    (catch NullPointerException e
      #_(error "Router not started: " e)
      #_(stacktrace/print-stack-trace e)
      packet)))


(defn get-router
  [args config]
  (let [mr-class-name (.getMessageRouterClassName config)]
    (.newInstance (Class/forName mr-class-name))))

(defmacro with-router
  [router & body]
  `(binding [jiksnu.xmpp.router/*message-router* ~router]
     ~@body))

(defn process!
  [^Packet packet]
  (.processPacket *message-router* packet))

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
