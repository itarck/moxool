(ns astronomy.data.stars
  (:require
   [cljs.reader :refer [read-string]]
   [astronomy.objects.star.m :as m.star])
  (:require-macros
   [methodology.lib.resource :refer [read-resource]]))


(def stars-data
  (->> (read-resource "edn/stars.edn")
       (read-string)
       (mapv m.star/parse-raw-bsc-data)))


(def dataset1 stars-data)

(comment 
  (first stars-data)
  )