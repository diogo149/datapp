(defproject datapp/core "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [org.clojure/tools.namespace "0.2.7"]
                 [org.clojure/data.json "0.2.5"]
                 [com.cognitect/transit-clj "0.8.259"]
                 [com.stuartsierra/component "0.2.2"]
                 [com.taoensso/timbre "3.3.1"]
                 [hiccup "1.0.5"]
                 [compojure "1.2.0"]
                 [ring "1.3.1"]
                 [ring/ring-json "0.3.1"]
                 [bk/ring-gzip "0.1.1"]
                 [ring-transit "0.1.2"]
                 [optimus "0.15.0"]
                 [http-kit "2.1.19"]
                 ;; cljs
                 [org.clojure/clojurescript "0.0-2322"]
                 [cljs-http "0.1.16"]]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.6.0"]]
                   :plugins [[lein-shell "0.4.0"]
                             [lein-cljsbuild "1.0.3"]
                             [com.keminglabs/cljx "0.4.0"
                              :exclusions [org.clojure/clojure]]
                        [edn-validator "0.2.0-SNAPSHOT"]]
                   :cljx {:builds [{:source-paths ["src/cljx"]
                                    :output-path "cljx-target/clj"
                                    :rules :clj}
                                   {:source-paths ["src/cljx"]
                                    :output-path "cljx-target/cljs"
                                    :rules :cljs}]}}}
  :jar-exclusions [#"\.cljx"]
  :source-paths ["cljx-target/clj" "src/clj"]
  :resource-paths ["cljx-target/cljs" "src/cljs"])
