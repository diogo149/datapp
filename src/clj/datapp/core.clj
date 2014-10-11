(ns datapp.core
  (:require [com.stuartsierra.component :as component]
            [datapp.handlers :as handlers]
            [datapp.ring.handler :as rhandler]
            [datapp.ring.middleware :as rmiddleware]
            [datapp.ring.server :as rserver]))

(def sample-app-config
  "This is an example of app-config, which should be overwritten"
  {:cljs/dev? false
   :cljs/ns "datapp.core"
   :cljs/react-js-path "/bower_components/react/react.js"
   :ring/server-port 14941
   :page/css-files []
   :page/js-files []
   :page/title "datapp Default Title"
   :optimus/dev? false
   :optimus/css-files []
   :optimus/js-files ["/cljs/all.js"]
   :optimus/assets []})

(def default-dependencies
  {:ring/request-deps [:app-config]
   :ring/middleware-comp [:app-config]
   :ring/handler-comp {:handlers :handlers
                       :handler-preds :ring/handler-preds
                       :middleware-map :ring/middleware-comp
                       :request-deps :ring/request-deps}
   :ring/server-comp {:app-config :app-config
                      :config :ring/server-config
                      :handler :ring/handler-comp}})

(def default-system
  {:app-config sample-app-config
   :handlers handlers/default-handlers
   :ring/middleware-comp rmiddleware/middleware-component
   :ring/handler-preds rhandler/default-handler-preds
   :ring/request-deps {}
   :ring/handler-comp rhandler/handler-component
   :ring/server-config rserver/default-config
   :ring/server-comp rserver/httpkit-server-component})

(defn use-depenencies
  "Adds dependencies to components that have dependencies"
  [system dependencies]
  (reduce (fn [sys [key deps]] (update-in sys [key] component/using deps))
          system
          dependencies))

(defn make-system
  [system dependencies]
  (->> (use-depenencies system dependencies)
       (apply concat)
       (apply component/system-map)))
