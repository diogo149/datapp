(ns datapp.handler
  (:require [com.stuartsierra.component :as component]
            [datapp.handler.default :as default-handler]))

(def default-handlers
  {:home-page default-handler/home-page
   :default default-handler/default-routes})

(def default-handler-filters
  [[:api #(->> % :uri (= "/api"))]
   [:home-page (fn [{:keys [request-method uri]}]
                 (and (= :get request-method)
                      (= "/" uri)))]
   [:default (constantly true)]])

(defn create-handler
  [{:keys [handlers handler-filters middleware]}]
  (reduce (fn [prev-handler [kw pred]]
            (let [this-handler (handlers kw)]
              (if-not this-handler
                prev-handler
                (let [wrapped-handler ((middleware kw identity) this-handler)]
                  (fn [req]
                    ((if (pred req)
                        wrapped-handler
                        prev-handler)
                     req))))))
          (fn [& _] (throw (Exception. "No default handler found")))
          (reverse handler-filters)))

(defrecord HandlerComponent [handler
                             handlers
                             handler-filters
                             middleware]
  component/Lifecycle
  (start [this]
    (assoc this :handler (create-handler
                          {:handlers handlers
                           :handler-filters handler-filters
                           :middleware (:middleware-map middleware)})))
  (stop [this]
    (dissoc this :handler)))

(def handler-component (map->HandlerComponent {}))
