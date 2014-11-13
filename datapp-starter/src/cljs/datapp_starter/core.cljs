(ns datapp-starter.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [datapp.ajax :as ajax]))

(js/console.log "foo")
(defn call
  []
  (go (println (<! (ajax/api {:choo #{34 {:foo :bar}}})))))
(call)
