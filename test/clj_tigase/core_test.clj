(ns clj-tigase.core-test
  (:use [clj-tigase.core :only [make-packet set-packet]]
        [midje.sweet :only [contains every-checker fact future-fact =>]])
  (:import tigase.server.Packet))

(fact "#'make-packet"
  (let [packet-map {:type :set}]
    (make-packet packet-map) =>
    (every-checker
     (partial instance? Packet))))

(fact "#'set-packet"
  (let [request {:to .to. :from .from. :id .id.}]
    (set-packet request .body.) =>
    (contains {:to .from. :from .to. :id .id. :body .body. :type :set})))

;; (deftest make-packet-test
;;   (testing "should return a packet"
;;     (let [user (model.user/create (factory User))
;;           packet-map {:to (make-jid user)
;;                       :from (make-jid user)
;;                       :type :get
;;                       :body (make-element
;;                              ["pubsub" {}])}
;;           response (make-packet packet-map)]
;;       (is (packet? response)))))
