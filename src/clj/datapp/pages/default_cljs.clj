(ns datapp.pages.default-cljs
  (:require [hiccup.core :as hiccup]
            optimus.link))

(defn cljs-home-page
  [{:keys [cljs/dev? cljs/ns page/css-files page/js-files page/title] :as req}]
  (hiccup/html
   (list
    "<!DOCTYPE html>"
    [:html
     [:head
      [:meta {:charset "utf-8"}]
      [:meta {:http-equiv "X-UA-Compatible"
              :content "IE=edge,chrome=1"}]
      [:meta
       {:name "viewport"
        :content "width=device-width, initial-scale=1.0, maximum-scale=2.0"}]
      [:link {:rel "shortcut icon", :href "/icon/favicon.ico"}]
      (for [css-file (concat (optimus.link/bundle-paths req ["all.css"])
                             css-files)]
        [:link {:rel "stylesheet"
                :type "text/css"
                :href css-file}])
      [:title title]]
     [:body
      [:div {:id "cljs-target"}]
      (when dev?
        [:script {:type "text/javascript"
                  :src "/cljs/dev/goog/base.js"}])
      (for [js-file (concat (optimus.link/bundle-paths req ["all.js"])
                            js-files)]
        [:script {:type "text/javascript"
                  :src js-file}])
      (when dev?
        [:script {:type "text/javascript"}
         (format "goog.require(\"%s\");" ns)])]])))
