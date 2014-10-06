(ns datapp.server
  (:require [com.stuartsierra.component :as component]
            [org.httpkit.server :as httpkit]
            [taoensso.timbre :as log]))

(def default-config
  {:thread (* 2 (.availableProcessors (Runtime/getRuntime)))
   :worker-name-prefix "server-"
   :port 14941})

(defrecord ServerComponent [server ;; callback to cancel started server
                            server-config
                            handler-comp]
  component/Lifecycle
  (start [this]
    (log/info "Starting server on port" (:port server-config))
    (when server
      (server))
    (assoc this :server (httpkit/run-server (:handler handler-comp)
                                            server-config)))
  (stop [this]
    (log/info "Stopping server")
    (server)
    (dissoc this :server)))

(def server-component (map->ServerComponent {}))
