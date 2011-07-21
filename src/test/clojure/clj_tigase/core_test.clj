(ns clj-tigase.core-test
  (:use clj-tigase.core
        clojure.test))

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

(deftest set-packet-test)

(deftest result-packet-test)

(deftest respond-with-test)

(deftest make-jid-test)

(deftest node-value-test)

(deftest deliver-packet!-test)

(deftest get-router-test)

(deftest with-router-test)

(deftest process!-test)

(deftest get-config-test)

(deftest start-router!-test)
