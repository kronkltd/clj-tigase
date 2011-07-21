(ns clj-tigase.element-test
  (:use clj-tigase.element
        [lazytest.describe :only (describe do-it testing)]
        [lazytest.expect :only (expect)]))

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

;; (describe to-tigase-element
;;   (testing "a simple element"
;;     (do-it "should"
;;       (let [element
;;             {:tag :query,
;;              :attrs {:xmlns "http://onesocialweb.org/spec/1.0/vcard4#query"},
;;              :content nil}]
;;         (expect (element? (to-tigase-element element))))))
;;   (testing "a full entry" {:focus true}
;;     (do-it "should return a tigase element"
;;       (with-format :atom
;;         (with-serialization :http
;;           (let [activity (factory Activity)
;;                 element (show-section activity)
;;                 response (to-tigase-element element)]
;;             (expect (element? response))))))))

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

(describe children)

(describe merge-namespaces)

(describe pubsub-element?)
