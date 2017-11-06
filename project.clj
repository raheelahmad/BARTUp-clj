(defproject clj-bart "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.854"]
                 [reagent "0.7.0"]
                 [cljs-ajax "0.7.2"]
                 [cljsjs/d3 "4.3.0-5"]]

  :min-lein-version "2.7.1"

  :target-path "target/%s"

  :plugins [[lein-cljsbuild "1.1.7"]] ;; compiler to JS

  ;; find compilable files
  :source-paths ["frontend/cljs"]

  ;; only clean compiled & target dirs; forget about the whole project
  :clean-targets ^{:protect false} ["resources/public/js"
                                    "target"]

  :figwheel {:http-server-root "resources/public"
             :nrepl-port 7002
             :css-dirs ["resources/public/css"]
             :nrepl-middleware ["cemerick.piggieback/wrap-cljs-repl"]}

  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :profiles {
             :dev {:dependencies [[figwheel-sidecar "0.5.13"]
                                  [com.cemerick/piggieback "0.2.2"] ;; needed for nREPL
                                  [binaryage/dirac "1.2.16"]
                                  ]
                   :plugins [[lein-figwheel "0.5.13"]
                             [lein-doo "0.1.7"]]
                   }}

  :cljsbuild {:builds
              {:dev {:source-paths ["frontend/cljs"]
                            :figwheel {:on-jsload "bart.core/mount-root"}
                            :compiler {:main bart.core
                                       :output-to "resources/public/js/app.js"
                                       :output-dir  "resources/public/js/out"
                                       :asset-path "js/out"
                                       :source-map-timestamp true}}
              :prod {:source-paths ["frontend/cljs"]
                     :compiler {:main bart.core
                                :output-to "resources/public/js/app.js"
                                :asset-path "js/out"
                                :optimizations :advanced
                                :pretty-print true
                                :pseudo-names true
                                }}
              }})
