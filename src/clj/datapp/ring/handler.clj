(ns datapp.ring.handler
  (:require [com.stuartsierra.component :as component]
            datapp.export))

(def default-handler-preds
  [[:api #(->> % :uri (= "/api"))]
   [:home-page (fn [{:keys [request-method uri]}]
                 (and (= :get request-method)
                      (= "/" uri)))]
   [:default (constantly true)]])

(defn create-handler
  [{:keys [handlers handler-preds middleware-map]}]
  (reduce (fn [prev-handler [kw pred]]
            (let [this-handler (handlers kw)]
              (if-not this-handler
                prev-handler
                (let [middleware (middleware-map kw identity)
                      wrapped-handler (middleware this-handler)]
                  (fn [req]
                    ((if (pred req)
                        wrapped-handler
                        prev-handler)
                     req))))))
          (fn [& _] (throw (Exception. "No default handler found")))
          (reverse handler-preds)))

(defrecord HandlerComponent [handler
                             handlers
                             handler-preds
                             middleware-map]
  component/Lifecycle
  (start [this]
    (assoc this :handler (create-handler this)))
  (stop [this]
    (dissoc this :handler))
  datapp.export/Exportable
  (export [this] handler))

(def handler-component (map->HandlerComponent {}))
