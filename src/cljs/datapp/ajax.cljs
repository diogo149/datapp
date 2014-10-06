(ns datapp.ajax
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]))

(def ^:dynamic *base-url*
  "Used for non-web applications when the base-url isn't necessarily set
  (e.g. with Apache Cordova)"
  nil)

(defn api
  ([data] (api data true))
  ([data relative?]
     (go (let [url (str (when relative? *base-url*) "/api")
               {:keys [status body]} (<! (http/post url {:transit-params data}))]
           (when (= status 200)
             body)))))
