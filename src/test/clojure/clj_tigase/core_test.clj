(ns clj-tigase.core-test
  (:use clj-tigase.core
        [lazytest.describe :only (describe do-it testing)]
        [lazytest.expect :only (expect)]
        )
  )

(describe packet?)

(describe element?)

(describe parse-qname)

(describe ns-prefix
  (testing "when the key name is empty"
    (do-it "should have just the xmlns"
      (let [k ""
            response (ns-prefix k)]
        (expect (= response "xmlns"))))))


(describe element-name)

(describe make-element-qname)

(describe get-qname)

(describe assign-namespace)

(describe process-child)

;; (describe make-element
;;   (testing "with a complex structure"
;;     (do-it "should return an element"
;;       (let [element-vec
;;             ["iq" {"type" "get"}
;;              ["pubsub" {"xmlns" pubsub-uri}
;;               ["items" {"node" microblog-uri}
;;                ["item" {"id" "test-id"}]]]]
;;             response  (make-element element-vec)]
;;         (expect (element? response))))))

(describe to-tigase-element)

(describe children)

(describe merge-namespaces)

(describe add-attributes)

;; (describe make-packet
;;   (do-it "should return a packet"
;;     (let [user (model.user/create (factory User))
;;           packet-map {:to (make-jid user)
;;                       :from (make-jid user)
;;                       :type :get
;;                       :body (make-element
;;                              ["pubsub" {}])}
;;           response (make-packet packet-map)]
;;       (expect (packet? response)))))

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

(describe pubsub-element?)

(describe set-packet)

(describe result-packet)

(describe respond-with)

(describe make-jid)

(describe node-value)

(describe make-request
  (testing "a pubsub publish"
    (do-it "should return a map"
      (let [packet (make-packet
                    {:to (make-jid "foo" "example.com")
                     :from (make-jid "bar" "example.com")
                     :type :get})
            response (make-request packet)]
        (expect (map? response))))))


(describe deliver-packet!)

(describe get-router)

(describe with-router)

(describe process!)

(describe get-config)

(describe start-router!)
