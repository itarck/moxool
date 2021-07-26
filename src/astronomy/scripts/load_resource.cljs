(ns astronomy.scripts.load-resource
  (:require
   [cljs.reader :refer [read-string]])
  (:require-macros [methodology.lib.resource :refer [read-resource]]))

(def stars
  (->
   (read-resource "edn/stars.edn")
   (read-string)))

(def constellation1
  (->
   (read-resource "edn/constellation1.edn")
   (read-string)))


constellation1