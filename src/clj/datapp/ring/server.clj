(ns datapp.ring.server
  (:require [com.stuartsierra.component :as component]
            [org.httpkit.server :as httpkit]
            [taoensso.timbre :as log]))

(def default-config
  {:thread (* 2 (.availableProcessors (Runtime/getRuntime)))
   :worker-name-prefix "server-"})

(defrecord HttpkitServerComponent [server ;; callback to cancel started server
                                   config
                                   app-config
                                   handler]
  component/Lifecycle
  (start [this]
    (let [port (:ring/server-port app-config)]
      (log/info (str "Starting server on http://localhost:" port))
      (when server
        (server))
      (assoc this :server (httpkit/run-server handler (assoc config
                                                        :port port)))))
  (stop [this]
    (log/info "Stopping server")
    (server)
    (dissoc this :server)))

(def httpkit-server-component (map->HttpkitServerComponent {}))
