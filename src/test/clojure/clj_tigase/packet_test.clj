(ns clj-tigase.packet-test
  (:use clj-tigase.packet
        [lazytest.describe :only (describe do-it testing)]
        [lazytest.expect :only (expect)]))

(describe packet?)

(describe iq-elements)

;; TODO: make generic
(describe pubsub-items
  (do-it "should return a seq of elements"
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
      (expect (every? element? response)))))

(describe bare-recipient?)

(describe get-items)

(describe make-request
  (testing "a pubsub publish"
    (do-it "should return a map"
      (let [packet (make-packet
                    {:to (make-jid "foo" "example.com")
                     :from (make-jid "bar" "example.com")
                     :type :get})
            response (make-request packet)]
        (expect (map? response))))))


