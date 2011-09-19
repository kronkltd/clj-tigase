(ns clj-tigase.element
  (:require [clojure.string :as string])
  (:import javax.xml.namespace.QName
           tigase.server.Packet
           tigase.xml.Element))

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

(defn ^String element-name
  [name prefix]
  (str (if (not= prefix "")
         (str prefix ":"))
       name))

(defn make-element-qname
  [{:keys [name prefix]}]
  (Element. (element-name name prefix)))

(defn assign-namespace
  [^Element element
   namespace-map
   [k v]]
  (if (not= (get namespace-map k) v)
    (do (.addAttribute
         element (ns-prefix k) v)
        [k v])))

(declare make-element)
(declare to-tigase-element)

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
          (.setCData element (string/trim item))
          (if (coll? item)
            (doseq [i item]
              (process-child element i))))))))

(defn ^Element to-tigase-element
  "turns a map into a tigase element"
  [{:keys [tag attrs content]}]
  (let [attribute-names (into-array String (map name (keys attrs)))
        attribute-values (into-array String (vals attrs))
        tag-name (name tag)
        element (Element. tag-name attribute-names attribute-values)]
    (doseq [item content]
      (process-child element item))
    element))

(defn make-element
  "Create a tigase element"
  ([spec]
     (apply make-element spec))
  ([name attrs]
     (make-element name attrs nil))
  ([^String name attrs & children]
     (let [element (Element. name)]
       (doseq [[attr val] attrs]
         (.addAttribute element attr (str val)))
       (doseq [child children]
         (process-child element child))
       element)))

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

 (defn pubsub-element?
  [^Element element]
  (and element
       (= (.getName element) "pubsub")))

(defn node-value
  [#^Element element]
  (.getAttribute element "node"))
