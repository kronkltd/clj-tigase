(ns clj-tigase.packet-test
  (:use clj-tigase.core
        clj-tigase.element
        clj-tigase.packet
        clojure.test))

(deftest packet?-test)

(deftest iq-elements-test)

;; TODO: make generic
(deftest pubsub-items-test
  (testing "should return a seq of elements"
    (let [element (make-element
                   ["pubsub" {}
                    ["items" {}
                     ["item" {} "foo"]
                     ["item" {} "bar"]]])
          packet (make-packet
                  {:to (make-jid "foo" "example.com")
                   :from (make-jid "bar" "example.com")
                   :type :set
                   :body element})
          response (pubsub-items packet)]
      (is (every? element? response)))))

(deftest bare-recipient?-test)

(deftest get-items-test)

(deftest make-request-test
  (testing "a pubsub publish"
    (testing "should return a map"
      (let [packet (make-packet
                    {:to (make-jid "foo" "example.com")
                     :from (make-jid "bar" "example.com")
                     :type :get})
            response (make-request packet)]
        (is (map? response))))))


