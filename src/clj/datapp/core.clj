(ns datapp.core
  (:require [com.stuartsierra.component :as component]
            [datapp.handler :as handler]
            [datapp.middleware :as middleware]
            [datapp.server :as server]))

(def sample-app-config
  "This is an example of app-config, which should be overwritten"
  {
   :optimus/dev? false
   :optimus/css-files []
   :optimus/js-files ["/cljs/all.js"]
   :optimus/assets []
   :cljs/dev? false
   :cljs/ns "datapp.core"
   :page/css-files []
   :page/js-files []
   :page/title "datapp Default Title"
   :cljs/react-js-path "/bower_components/react/react.js"
   })

(def default-dependencies
  {:middleware [:app-config]
   :handler-comp [:handlers :handler-filters :middleware]
   :server-comp [:server-config :handler-comp]})

(def default-system
  {
   ;; app/config
   :app-config sample-app-config
   ;; server/middleware
   :middleware middleware/middleware-component
   ;; server/handlers
   :handlers handler/default-handlers
   ;; server/filters
   :handler-filters handler/default-handler-filters
   ;; handler???
   :handler-comp  handler/handler-component
   ;; server/config
   :server-config server/default-config
   ;; server/component
   :server-comp server/server-component
   })

(defn make-system
  [system-overrides]
  (->> system-overrides
       (merge default-system)
       (apply concat)
       (apply component/system-map)))
