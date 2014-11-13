(defproject datapp-starter "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :jvm-opts ["-Xmx1g"]
  :min-lein-version "2.0.0"
  :profiles
  {:dev [:clj
         {:dependencies [[org.clojure/tools.namespace "0.2.5"]]
          :plugins [[lein-shell "0.4.0"]]
          :source-paths ["src/dev"]
          :repl-options {:init-ns user}
          :aliases
          { ;; cleaning
           "clean-cljs" ["shell" "rm" "-rf" "resources/public/cljs" ","]
           "clean-cljx" ["shell" "rm" "-rf" "cljx-target/" ","]
           "clean-all" ["do" "clean," "clean-cljs," "clean-cljx,"]
           ;; clojurescript
           "cljs1" ["do" "clean-cljs,"
                    "with-profile" "cljs" "cljsbuild" "once" "prod,"]
           "cljs" ["do" "clean-cljs,"
                   "with-profile" "cljs" "cljsbuild" "auto" "dev"]
           ;; cljx
           "cljx1" ["do" "clean-cljx,"
                    "with-profile" "cljx" "cljx" "once,"]
           "cljx" ["do" "clean-cljx,"
                   "with-profile" "cljx" "cljx" "auto,"]
           ;; clj
           "clj" ["do" "clean,"
                  "repl,"]
           "uberjar" ["do" "clean-all,"
                      "cljx1,"
                      "cljs1,"
                      "with-profile" "uberjar" "uberjar,"]
           "run-all" ["do", "clean-all,"
                      "cljx1,"
                      "cljs1,"
                      "run,"]}}]
   :cljx {:plugins [[com.keminglabs/cljx "0.4.0"]]
          :cljx {:builds [{:source-paths ["src/cljx"]
                           :output-path "cljx-target/cljs"
                           :rules :cljs}
                          {:source-paths ["src/cljx"]
                           :output-path "cljx-target/clj"
                           :rules :clj}]}}
   :shared {:dependencies [[org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                           [datapp/core "0.1.0-SNAPSHOT"]]}
   :clj [:shared
         {:main datapp-starter.core
          :dependencies []
          :source-paths ["src/clj"
                         ;; "cljx-target/clj"
                         ;; "src/macros"
                         ]}]
   :cljs [:shared
          {:dependencies [[org.clojure/clojurescript "0.0-2322"]
                          [reagent "0.4.2"]]
           :plugins [[lein-cljsbuild "1.0.3"]]
           :cljsbuild
           {:builds [{:id "dev"
                      :source-paths ["src/cljs"
                                     ;; "cljx-target/cljs"
                                     ;; "src/macros"
                                     ]
                      :compiler
                      {:output-to "resources/public/cljs/all.js"
                       :output-dir "resources/public/cljs/dev"
                       :optimizations :none
                       :source-map "resources/public/cljs/all.js.map"
                       :externs ["react/externs/react.js"]}}
                     {:id "prod"
                      :source-paths ["src/cljs"
                                     ;; "src/macros"
                                     ;; "cljx-target/cljs"
                                     ]
                      :compiler
                      {:output-to "resources/public/cljs/all.js"
                       :output-dir "resources/public/cljs/prod"
                       :optimizations :advanced
                       :pretty-print false
                       :output-wrapper false
                       :preamble ["reagent/react.min.js"]
                       :externs [ ;; "jquery/externs/jquery.js"
                                 "src/js/extern.js"]
                       :closure-warnings
                       {:non-standard-jsdoc :off}}}]}}]
   :uberjar [:clj
             {:aot [;; for some reason, this is necessary along with the regex
                    datapp-starter.core
                    #"datapp-starter.*"]
              :uberjar-name "datapp-starter-standalone.jar"}]})
