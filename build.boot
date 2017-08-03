(set-env!
  :source-paths #{"src"}
  :resource-paths #{"resources"}
  :dependencies '[[org.clojure/clojure "1.8.0"]
                  [org.clojure/clojurescript "1.9.854"]

                  [adzerk/boot-cljs       "2.1.1"  :scope "test"]
                  [adzerk/boot-cljs-repl  "0.3.3"      :scope "test"]
                  [com.cemerick/piggieback "0.2.2"     :scope "test"]
                  [weasel                 "0.7.0"      :scope "test"]
                  [org.clojure/tools.nrepl "0.2.13"    :scope "test"]
                  [adzerk/boot-reload "0.5.1" :scope "test"]
                  [deraen/boot-sass "0.3.1" :scope "test"]
                  [org.slf4j/slf4j-nop "1.7.25" :scope "test"]
                  [metosin/boot-alt-http "0.1.2" :scope "test"]

                  ; Frontend
                  [reagent "0.8.0-alpha1"]
                  [metosin/komponentit "0.3.0"]
                  [binaryage/devtools "0.9.4"]])

(require
  '[adzerk.boot-cljs      :refer [cljs]]
  '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl repl-env]]
  '[adzerk.boot-reload    :refer [reload]]
  '[deraen.boot-sass      :refer [sass]]
  '[metosin.boot-alt-http :refer [serve]])

(task-options!
  pom {:project 'cljsjs.github.io
       :version "0.1.0-SNAPSHOT"
       :description "Application template for Cljs/Om with live reloading, using Boot."
       :license {"The MIT License (MIT)" "http://opensource.org/licenses/mit-license.php"}})

(deftask dev
  "Start the dev env..."
  []
  (comp
    (watch)
    (sass)
    (reload)
    (cljs-repl)
    (cljs)
    (serve :prefixes #{""})))

(deftask build
  "Build the package"
  []
  (comp
    (sass)
    (cljs :optimizations :advanced)
    (sift :invert true :include #{#"main\.out/"})
    (target :dir #{"build"})))
