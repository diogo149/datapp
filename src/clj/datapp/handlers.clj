(ns datapp.handlers
  (:require [compojure.core :as compojure]
            [compojure.route :as route]
            [datapp.utils.ring :as ringu]
            datapp.pages.default-cljs))

(defn home-page
  [req]
  (datapp.pages.default-cljs/cljs-home-page req))

(defn reagent-home-page
  [{:keys [cljs/dev? cljs/react-js-path] :as req}]
  (let [req (if-not dev?
              req
              (-> req
                  (update-in [:page/js-files] conj react-js-path)))]
    (home-page req)))

(compojure/defroutes default-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def default-handlers
  {:api (fn [& args] "API not implemented")
   :home-page home-page
   :default default-routes})
