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

(describe assign-namespace)

(describe process-child)

(describe to-tigase-element)

(describe children)

(describe merge-namespaces)

(describe add-attributes)

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
