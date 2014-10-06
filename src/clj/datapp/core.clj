(ns datapp.core
  (:require [com.stuartsierra.component :as component]
            [datapp.handler :as handler]
            [datapp.middleware :as middleware]
            [datapp.server :as server]))

(def sample-app-config
  "This is an example of app-config, which should be overwritten"
  {:optimus/dev? false
   :optimus/css-files []
   :optimus/js-files []
   :optimus/assets []
   :cljs/dev? false
   :cljs/ns "datapp.core"
   :page/css-files []
   :page/js-files []
   :page/title "datapp Default Title"
   :cljs/react-js-path "/bower_components/react/react.js"})

(def default-system
  {:app-config sample-app-config
   :middleware (component/using
                middleware/middleware-component
                [:app-config])
   :handlers handler/default-handlers
   :handler-filters handler/default-handler-filters
   :handler-comp (component/using
                  handler/handler-component
                  [:handlers :handler-filters :middleware])
   :server-config server/default-config
   :server-comp (component/using
                 server/server-component
                 [:server-config :handler-comp])})

(defn make-system
  [system-overrides]
  (->> system-overrides
       (merge default-system)
       (apply concat)
       (apply component/system-map)))
