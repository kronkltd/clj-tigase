(ns clj-tigase.core-test
  (:use clj-tigase.core
        [lazytest.describe :only (describe do-it testing)]
        [lazytest.expect :only (expect)]))

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

(describe set-packet)

(describe result-packet)

(describe respond-with)

(describe make-jid)

(describe node-value)

(describe deliver-packet!)

(describe get-router)

(describe with-router)

(describe process!)

(describe get-config)

(describe start-router!)
