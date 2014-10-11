(ns datapp.utils.timer)

(defn set-interval
  [f msec]
  (js/setInterval f msec))

(defn clear-interval
  [i]
  (js/clearInterval i))

(defn set-timeout
  [f msec]
  (js/setTimeout f msec))

(defn clear-timeout
  [i]
  (js/clearTimeout i))

(let [intervals (atom {})]
  (defn get-intervals
    "Get the available interval keys"
    []
    (keys @intervals))
  (defn pop-interval
    "Clears an interval; like js/clearInterval"
    [key]
    (swap! intervals
           (fn [x]
             (when-let [interval (get x key)] (clear-interval interval))
             (dissoc x key)))
    key)
  (defn unique-interval
    "Like js/setInterval, but makes sure that an interval is only running once;
     doesn't rerun if already running"
    [key f msec]
    (swap! intervals
           (fn [x]
             (if (contains? x key) x
                 (assoc x key (set-interval f msec)))))
    key)
  (defn overwrite-interval
    "Like js/setInterval, but makes sure that an interval is only running once;
     does rerun if already running"
    [key f msec]
    (pop-interval key)
    (unique-interval key f msec))
  (defn push-interval
    "Like js/setInterval"
    [f msec]
    (unique-interval (gensym) f msec))
  (defn cancel-intervals
    "Cancels all existing intervals"
    []
    (doseq [k (get-intervals)]
      (pop-interval k))))

(let [timeouts (atom {})]
  (defn get-timeouts
    "Get the available timeout keys"
    []
    (keys @timeouts))
  (defn pop-timeout
    "Clears a timeout; like js/clearTimeout"
    [key]
    (swap! timeouts
           (fn [x]
             (when-let [timeout (get x key)] (clear-timeout timeout))
             (dissoc x key)))
    key)
  (defn unique-timeout
    "Like js/setTimeout, but makes sure that a timeout is only running once;
     doesn't rerun if already running"
    [key f msec]
    (swap! timeouts
           (fn [x]
             (if (contains? x key) x
                 (assoc x key (set-timeout #(do (pop-timeout key) (f))
                                           msec)))))
    key)
  (defn overwrite-timeout
    "Like js/setTimeout, but makes sure that a timeout is only running once;
     does rerun if already running"
    [key f msec]
    (pop-timeout key)
    (unique-timeout key f msec))
  (defn push-timeout
    "Like js/setTimeout"
    [f msec]
    (unique-timeout (gensym) f msec))
  (defn cancel-timeouts
    "Cancels all existing timeouts"
    []
    (doseq [k (get-timeouts)]
      (pop-timeout k))))
