(ns astronomy.model.star
  (:require 
   [datascript.core :as d]
   [posh.reagent :as p]))



;; schema

(def schema #:star{:name {:db/unique :db.unique/identity}
                   :HD {:db/unique :db.unique/identity}
                   :HR {:db/unique :db.unique/identity}})


;; data 

(def sample1
  #:star{:name "sun"
         :color "red"
         :radius 20
         :object/position [0 0 0]
         :object/quaternion [0 0 0 1]})

(def sample2
  #:star {:HR 1
          :HD 3
          :RAh 0
          :RAm 5
          :RAs 9.9
          :right-ascension 0.08608333333333333
          :DEd 45
          :DEm 13
          :DEs 45
          :declination 45.22916666666667
          :visual-magnitude 6.7})


;; model
 

(defn parse-raw-bsc-data [star]
  (let [{:star/keys [RAh RAm RAs DEd DEm DEs]} star
        right-ascension (+ (* (/ RAh 24.0) 360.0)
                           (/ RAm 60.0)
                           (/ RAs 3600.0))
        declination (+ DEd
                       (/ DEm 60.0)
                       (/ DEs 3600.0))]
    (-> star
        (assoc :star/right-ascension right-ascension)
        (assoc :star/declination declination))))


(defn find-all-stars [db]
  (let [ids (d/q '[:find [?id ...]
                   :where
                   [?id :star/HR _]]
                 db)]
    (mapv (fn [id] (d/pull db '[*] id)) ids)))

;; subs

(defn sub-planets [conn star]
  (let [star @(p/pull conn '[* {:planet/_star [*]}] (:db/id star))]
    (:planet/_star star)))
  
(defn sub-world-position [conn star-id]
  (let [star @(p/pull conn '[*] star-id)]
    (:object/position star)))


(comment 
  (parse-raw-bsc-data sample2)
  ;; => #:star{:DEm 13, :HR 1, :RAh 0, :RAm 5, :DEd 45, :HD 3, :right-ascension 0.08608333333333333, :visual-magnitude 6.7, :DEs 45, :RAs 9.9, :declination 45.22916666666667}

  )