(ns clj-tigase.packet-test
  (:use [clj-tigase.core :only [make-jid make-packet]]
        [clj-tigase.element :only [element? make-element]]
        [clj-tigase.packet :only [make-request pubsub-items]]
        [midje.sweet :only [every-checker fact future-fact =>]]))

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
