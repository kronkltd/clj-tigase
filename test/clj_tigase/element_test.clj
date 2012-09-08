(ns clj-tigase.element-test
  (:use clj-tigase.element
        clojure.test))

(deftest ns-prefix-test
  (testing "when the key name is empty"
    (testing "should have just the xmlns"
      (let [k ""
            response (ns-prefix k)]
        (is (= response "xmlns"))))))


;; (deftest to-tigase-element-test
;;   (testing "a simple element"
;;     (testing "should"
;;       (let [element
;;             {:tag :query,
;;              :attrs {:xmlns "http://onesocialweb.org/spec/1.0/vcard4#query"},
;;              :content nil}]
;;         (is (element? (to-tigase-element element))))))
;;   (testing "a full entry" {:focus true}
;;     (testing "should return a tigase element"
;;       (with-format :atom
;;         (with-serialization :http
;;           (let [activity (factory Activity)
;;                 element (show-section activity)
;;                 response (to-tigase-element element)]
;;             (is (element? response))))))))

;; (deftest make-element-test
;;   (testing "with a complex structure"
;;     (testing "should return an element"
;;       (let [element-vec
;;             ["iq" {"type" "get"}
;;              ["pubsub" {"xmlns" pubsub-uri}
;;               ["items" {"node" microblog-uri}
;;                ["item" {"id" "test-id"}]]]]
;;             response  (make-element element-vec)]
;;         (is (element? response))))))

