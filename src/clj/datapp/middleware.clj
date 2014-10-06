(ns datapp.middleware
  (:require [com.stuartsierra.component :as component]
            [datapp.utils.ring :as ringu]
            ring.middleware.params
            ring.middleware.keyword-params
            ring.middleware.json
            ring.middleware.gzip
            ring.middleware.content-type
            ring.middleware.not-modified
            [optimus.prime :as optimus]
            [optimus.assets :as assets]
            [optimus.optimizations :as optimizations]
            [optimus.strategies :as strategies]))

(defn wrap-with-optimus
  [handler app-config]
  (let [{:keys [optimus/dev? optimus/css-files
                optimus/js-files optimus/assets]} app-config
        get-assets (fn [] (concat
                           (assets/load-bundle "public" "all.css" css-files)
                           (assets/load-bundle "public" "all.js" js-files)
                           (assets/load-assets "public" assets)))]
    (-> handler
        (optimus/wrap
         get-assets
         (if dev? optimizations/none optimizations/all)
         (if dev?
           strategies/serve-live-assets
           strategies/serve-frozen-assets))
        ring.middleware.content-type/wrap-content-type
        ring.middleware.not-modified/wrap-not-modified)))

(defn wrap-defaults
  "Default middleware that could be used for any request"
  [handler app-config]
  (-> handler
      (ringu/wrap-merge-constants app-config)
      ring.middleware.gzip/wrap-gzip
      ringu/wrap-fix-request
      ringu/wrap-request-logging))

(defn default-middleware-map
  [{:keys [] :as app-config}]
  {:api #(-> %
             ring.middleware.keyword-params/wrap-keyword-params
             ring.middleware.json/wrap-json-params
             ring.middleware.params/wrap-params
             (wrap-defaults app-config))
   :home-page #(-> %
                   ringu/wrap-html-response
                   (wrap-with-optimus app-config)
                   (wrap-defaults app-config))
   :default #(-> %
                 (wrap-with-optimus app-config)
                 (wrap-defaults app-config))})

(defrecord MiddlewareComponent [app-config
                                middleware-map]
  component/Lifecycle
  (start [this]
    (assoc this :middleware-map (default-middleware-map app-config)))
  (stop [this]
    (dissoc this :middleware-map)))

(def middleware-component (map->MiddlewareComponent {}))

(comment
  (def last-request
  "for debugging"
  (atom nil))

  (defn wrap-api-uri
    [handler]
    (fn [req]
      (reset! last-request req)
      (if-not (and (->> req :request-method (= :post))
                   (->> req :uri (re-find #"^/api")))
        (handler req)
        ((->> api/handle-api
              ((fn [handler] (fn [req] (-> req :params handler))))
              serveru/wrap-unified-edn-api)
         req)))))
