(ns astronomy.objects.star.m
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [shu.three.vector3 :as v3]
   [shu.goog.math :as gmath]
   [shu.astronomy.light :as shu.light]
   [astronomy.objects.celestial.m :as m.celestial]))



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
        right-ascension (+ (* RAh (/ 360 24.0))
                           (* RAm (/ 360 24.0 60.0))
                           (* RAs (/ 360 24.0 3600.0)))
        declination (if (>= DEd 0)
                      (+ DEd (/ DEm 60.0) (/ DEs 3600.0))
                      (- DEd (/ DEm 60.0) (/ DEs 3600.0)))]
    (-> star
        (assoc :star/right-ascension right-ascension)
        (assoc :star/declination declination))))

(defn find-all-stars [db]
  (let [ids (d/q '[:find [?id ...]
                   :where
                   [?id :star/HR _]]
                 db)]
    (mapv (fn [id] (d/pull db '[*] id)) ids)))


(defn visual-magnitude->length [vm]
  (* (* 100 shu.light/light-year-unit) (Math/pow 10 (/ vm 8))))


(defn cal-star-position-vector [star]
  (let [{:star/keys [visual-magnitude right-ascension declination]} star
        radius (visual-magnitude->length visual-magnitude)]
    (v3/from-spherical-coords
     radius
     (gmath/to-radians (- 90 declination))
     (gmath/to-radians right-ascension))))



;; 实现接口

(defmethod m.celestial/cal-system-position-now :star
  [_db star]
  (:object/position star))

(defmethod m.celestial/cal-system-position-at-epoch :star
  [db star epoch-days]
  (:object/position star))

;; subs

(defn sub-planets [conn star]
  (let [star @(p/pull conn '[* {:planet/_star [*]}] (:db/id star))]
    (:planet/_star star)))

(defn sub-all-constellation-star-ids [conn]
  (let [star-lines @(p/q '[:find [?star-lines ...]
                           :where [?id :constellation/star-lines ?star-lines]]
                         conn)]
    (->> star-lines
         (apply concat)
         (apply concat)
         distinct)))

(defn sub-all-constellation-stars [conn]
  (let [ids (sub-all-constellation-star-ids conn)]
    (doall
     (mapv (fn [id] @(p/pull conn '[*] id)) ids))))



(comment
  (parse-raw-bsc-data sample2)
  ;; => #:star{:DEm 13, :HR 1, :RAh 0, :RAm 5, :DEd 45, :HD 3, :right-ascension 0.08608333333333333, :visual-magnitude 6.7, :DEs 45, :RAs 9.9, :declination 45.22916666666667}


  (visual-magnitude->length 1)
  ;; => 158.48931924611136
  )