(defproject reagent-transition-group "0.0.1"
  :dependencies
  [[org.clojure/clojure "1.8.0"]
   [org.clojure/clojurescript "1.9.908"]
   [cljsjs/d3 "4.3.0-5"]
   [org.clojure/core.async "0.3.443"]
   [com.cemerick/piggieback "0.2.2"]
   [org.clojure/tools.nrepl "0.2.10"]
   [figwheel-sidecar "0.5.13"]
   [cljsjs/react-transition-group "1.1.3-0"]
   [reagent "0.7.0"]]

  :plugins
  [[lein-figwheel "0.5.13"]
   [lein-cljsbuild "1.1.7"]]

  :repl-options
  {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :cljsbuild
  {:builds
   [{:id "dev"
     :figwheel true
     :source-paths ["src"]
     :compiler {:main "reagent-transition-group.core"
                :asset-path "js/out"
                :output-to "resources/public/script.js"
                :output-dir "resources/public/js/out"}}
    {:id "min"
     :source-paths ["src"]
     :compiler {:output-to "resources/public/script.js"
                :source-map-timestamp true
                :optimizations :advanced
                :pretty-print false}}]})
