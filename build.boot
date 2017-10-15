(set-env!
 :source-paths    #{"frontend/cljs"}
 :resource-paths  #{"resources/public"}
 :dependencies '[[adzerk/boot-cljs          "2.0.0"      :scope "test"]
                 [adzerk/boot-cljs-repl     "0.3.3"      :scope "test"]
                 [adzerk/boot-reload        "0.5.1"      :scope "test"]
                 [pandeiro/boot-http        "0.8.3"      :scope "test"]
                 [com.cemerick/piggieback   "0.2.1"      :scope "test"]
                 [org.clojure/tools.nrepl   "0.2.13"     :scope "test"]
                 [weasel                    "0.7.0"      :scope "test"]
                 [org.clojure/clojurescript "1.9.562"]
                 [reagent "0.6.0"]
                 [cljs-ajax "0.7.2"]])

(require
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[pandeiro.boot-http    :refer [serve]])

(deftask dev
  []
  (comp
   (watch)
   (reload :on-jsload 'bart.core/init)
   (cljs-repl)
   (cljs :optimizations :none
         :source-map true)
   (target)
   (serve)
   (speak)
   ))

