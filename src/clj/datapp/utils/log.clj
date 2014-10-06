(ns datapp.utils.log)

(defmacro time-map
  [& code]
  `(let [start# (System/nanoTime)
         result# (do ~@code)
         ellapsed# (/ (double (- (System/nanoTime) start#)) 1000000.0)]
     {:time ellapsed# :result result#}))
