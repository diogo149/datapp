(ns datapp.utils.async
  #+clj (:require [clojure.core.async :as async :refer [go]])
  #+cljs (:require-macros [cljs.core.async.macros :refer [go]])
  #+cljs (:require [cljs.core.async :as async]))

(defn loop-ch
  "Continuously read from a given channel in a loop, applying a function
   to the result. Returns a function to cancel the loop"
  [f ch]
  (let [close-ch (async/chan)]
    (go (loop []
          (let [[val p] (async/alts! [ch close-ch])]
            (when (and val (not= p close-ch))
              (f val)
              (recur)))))
    #(async/put! close-ch ::close)))
