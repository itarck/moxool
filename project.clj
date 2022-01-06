(defproject moxool "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/test.check "1.1.0"]
                 [compojure "1.6.1"]
                 [funcool/cuerdas "2.2.0"]
                 [ring "1.8.1"]
                 [ring-range-middleware "0.1.0"]
                 [ring-middleware-format "0.7.4"]
                 [ring/ring-defaults "0.3.2"]
                 [ring-cors "0.1.13"]
                 [integrant "0.8.0"]
                 [hiccup "1.0.5"]
                 [http-kit "2.5.1"]
                 [ultra-csv "0.2.3"]]

  :source-paths ["src"]
  :resource-paths ["resources/public"]

  :profiles {:dev {:dependencies
                   [[integrant/repl "0.3.1"]
                    [ring/ring-mock "0.3.2"]]
                   :source-paths ["dev"]}})
