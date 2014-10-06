(ns datapp.utils.ring
  (:require [clojure.data.json :as json]
            ring.util.response
            [datapp.utils.random :as randomu]
            [taoensso.timbre :as log]
            [datapp.utils.log :as logu]
            [cognitect.transit :as transit]))

(defn transit-write-str
  "from https://github.com/swannodette/transit-example/blob/master/src/clj/transit_example/server.clj"
  [x]
  (let [baos (java.io.ByteArrayOutputStream.)
        w (transit/writer baos :json)
        _ (transit/write w x)
        ret (.toString baos)]
    (.reset baos)
    ret))

(defn transit-response
  [resp]
  (-> resp
      transit-write-str
      ring.util.response/response
      (ring.util.response/content-type "application/transit+json")))

(defn wrap-transit-response
  [handler]
  (fn [req]
    (transit-response (handler req))))

(defn json-response
  [resp]
  (-> resp
      ring.util.response/response
      json/write-str
      (ring.util.response/content-type "application/json")))

(defn html-response
  [resp]
  (-> resp
      ring.util.response/response
      (ring.util.response/content-type "text/html")))

(defn wrap-html-response
  [handler]
  (fn [req]
    (html-response (handler req))))

(defn wrap-fix-request
  "Meant for various transforms in the request to match expectations"
  [handler]
  (letfn [(fix-multiple-accept-encoding-headers [req]
            (if-not (list? (get-in req [:headers "accept-encoding"]))
              req
              ;; wrap-gzip expects the accept-encoding header to be a string
              ;; but if there are multiple accept-encoding headers, ring
              ;; transforms it into a list. not sure if this is the right thing
              ;; to do (merging the headers would be another option)
              (update-in req [:headers "accept-encoding"] first)))]
    (fn [request]
      (->> request
           fix-multiple-accept-encoding-headers
           handler))))

(defn wrap-request-logging
  "Middleware wrapper to log all incoming requests."
  [handler]
  (fn [{:keys [request-method uri session params] :as req}]
    (let [req-id (randomu/alphanumeric-string 10)]
      (log/info
       (format "REQUEST%s %s, session=%s, params=%s, req-id=%s"
               request-method uri session params req-id))
      (let [{:keys [time result]} (logu/time-map (handler req))]
        (log/debug (format "REQUEST%s %s, req-id=%s, time=%sms"
                           request-method uri req-id time))
        result))))

(defn wrap-merge-constants
  [handler map]
  (fn [req]
    (handler (merge map req))))
