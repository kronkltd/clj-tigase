(ns clj-tigase.packet
  (:use clj-tigase.element)
  (:import tigase.server.Packet
           tigase.xml.Element))

(defn packet?
  "Returns if the given object is a packet"
  [obj]
  (instance? Packet obj))

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
  (concat
   (children packet "/iq/pubsub/items")
   (children packet "/message/event/items")))

(defn make-request
  [^Packet packet]
  (let [type (keyword (str (.getType packet)))
        to (.getStanzaTo packet)
        from (.getStanzaFrom packet)
        ^Element payload  (first (iq-elements packet))
        pubsub? (pubsub-element? payload)
        ^Element child-node (first (children payload))
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
     :scheme :xmpp
     :method type
     :items (get-items packet)}))

