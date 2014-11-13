(ns datapp-starter.core
  (:require [datapp.core :as datapp]
            [com.stuartsierra.component :as component])
  (:gen-class))

(defn main-system
  [config]
  (datapp/make-system
   (-> datapp/default-system
       (update-in [:app-config] merge {:cljs/dev? true
                                       :cljs/ns "datapp_starter.core"})
       (update-in [:handlers] merge {:api (fn [arg req] (println "FOO" arg) #{:foo {:34 34}})}))
   datapp/default-dependencies))

(defn -main
  [& args]
  (component/start (main-system {})))
