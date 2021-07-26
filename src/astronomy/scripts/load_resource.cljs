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

(def constellation2
  (->
   (read-resource "edn/constellation2.edn")
   (read-string)))

(defn load-constellations2! [file-name]
  (let [constellations (->> (read-resource file-name)
                            (read-string))]
    constellations))

constellation2