(ns datapp.utils.random)

(defn uuid []
  #+clj
  (str (java.util.UUID/randomUUID))
  #+cljs
  (let [r (repeatedly 30 (fn [] (.toString (rand-int 16) 16)))]
    (apply str (concat (take 8 r) ["-"]
                       (take 4 (drop 8 r)) ["-4"]
                       (take 3 (drop 12 r)) ["-"]
                       [(.toString  (bit-or 0x8 (bit-and 0x3 (rand-int 15))) 16)]
                       (take 3 (drop 15 r)) ["-"]
                       (take 12 (drop 18 r))))))

(def ^:private alphanumeric-chars
  (map char (concat (range 48 58) ;; 0-9
                    (range 66 91) ;; A-Z
                    (range 97 123)))) ;; a-z

(defn alphanumeric-string
  [length]
  (->> #(rand-nth alphanumeric-chars)
       repeatedly
       (take length)
       (apply str)))
