(ns datapp.export
  (:require [com.stuartsierra.component :as component]))

(defprotocol Exportable
  (export [this]
    "Returns the output of a component that consuming components should use
     as a dependency"))

;; No-op implementation if one is not defined.
(extend-protocol Exportable
  java.lang.Object
  (export [this]
    this))

;; FIXME - find a long term solution to not using raw dependencies
;; copied from https://github.com/stuartsierra/component/blob/master/src/com/stuartsierra/component.clj on 20141009
(defn- assoc-dependency [system component dependency-key system-key]
  (let [dependency (get system system-key)]
    (when-not dependency
      (throw (ex-info (str "Missing dependency " dependency-key
                           " of " (.getName (class component))
                           " expected in system at " system-key)
                      {:reason ::missing-dependency
                       :system-key system-key
                       :dependency-key dependency-key
                       :component component
                       :system system})))
    (assoc component dependency-key (export dependency))))

;; NOTE: this is dangerous - it's a private method
(alter-var-root #'component/assoc-dependency
                (fn [_] assoc-dependency))
