(ns clj-tigase.packet-test
  (:require [clj-tigase.core :refer [make-jid make-packet]]
            [clj-tigase.element :refer [element? make-element]]
            [clj-tigase.packet :refer [make-request pubsub-items]]
            [midje.sweet :refer [every-checker fact future-fact =>]]))

;; TODO: make generic
(fact "#'pubsub-items"
  (let [element (make-element
                 ["pubsub" {}
                  ["items" {}
                   ["item" {} "foo"]
                   ["item" {} "bar"]]])
        packet-map {:to (make-jid "foo" "example.com")
                    :from (make-jid "bar" "example.com")
                    :type :set
                    :body element}
        packet (make-packet packet-map)]
    (pubsub-items packet) =>
    (every-checker
     (partial every? element?))))

(fact "#'make-request"
  (fact "a pubsub publish"
    (let [packet-map {:to (make-jid "foo" "example.com")
                      :from (make-jid "bar" "example.com")
                      :type :get}
          packet (make-packet packet-map)]
      (make-request packet) =>
      (every-checker
       map?))))
