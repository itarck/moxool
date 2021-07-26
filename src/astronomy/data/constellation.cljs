(ns astronomy.data.constellation
  (:require
   [cljs.reader :refer [read-string]])
  (:require-macros
   [methodology.lib.resource :refer [read-resource]]))



(def constellation-families
  [#:constellation-family {:chinese-name "黄道", :color "orange"}
   #:constellation-family {:chinese-name "英仙", :color "red"}
   #:constellation-family {:chinese-name "武仙", :color "green"}
   #:constellation-family {:chinese-name "大熊", :color "CornflowerBlue"}
   #:constellation-family {:chinese-name "猎户", :color "white"}
   #:constellation-family {:chinese-name "幻之水", :color "Aqua"}
   #:constellation-family {:chinese-name "拜耳", :color "yellow"}
   #:constellation-family {:chinese-name "拉卡伊", :color "HotPink"}])


(defn parse-star-line [HR-line]
  (mapv (fn [HR] [:star/HR HR]) HR-line))

(defn parse-constellation [constellation]
  (let [star-lines (vec
                    (for [line (:constellation/star-HR-lines constellation)]
                      (parse-star-line line)))
        abbreviation (:constellation/abbreviation constellation)]
    #:constellation {:abbreviation abbreviation
                     :star-lines star-lines}))


(defn load-constellations1 [constellation1-data]
  (let [tx (mapv (fn [constel] (parse-constellation constel)) constellation1-data)]
    tx))


(def constellation1-data
  (->> (read-resource "edn/constellation1.edn")
      (read-string)
      (mapv (fn [constel] (parse-constellation constel)))))

(def constellation2-data
  (-> (read-resource "edn/constellation2.edn")
      (read-string)))



(def dataset1 constellation-families)

(def dataset2 constellation1-data)

(def dataset3 constellation2-data)


(comment 
  
  constellation1-data

  ;; 
  )