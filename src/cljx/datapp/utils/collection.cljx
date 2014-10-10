(ns datapp.utils.collection
  (:require [clojure.core.reducers :as r]))

(defn safe-get
  "Like get, but makes sure that the key is present"
  [map key]
  (assert (contains? map key)
          "Map doesn't contain key in safe-get"))

(defn safe-assoc
  "Like assoc, but makes sure no keys are overwritten"
  [map & kvs]
  (let [assocd (apply assoc map kvs)]
    (assert (= (count assocd)
               (+ (count map)
                  (/ (count kvs) 2)))
            "Found duplicate keys in safe-assoc")))

(defn safe-merge
  "Like merge, but makes sure no keys are overwritten"
  [& maps]
  (let [merged (apply merge maps)]
    (assert (= (count merged)
               (reduce + (r/map count maps)))
            "Found duplicate keys in safe-merge")
    merged))
