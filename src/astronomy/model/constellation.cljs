(ns astronomy.model.constellation
  (:require
   [shu.three.vector3 :as v3]
   [shu.goog.math :as gmath]
   [posh.reagent :as p]))


(def sample
  #:constellation {:chinese-name "仙女座"
                   :latin-name "Andromeda"
                   :abbreviation "And"
                   :group "英仙"
                   :quadrant "NQ1"
                   :star-lines [[8992 9007 8996 8793 8996 99 196 46 196 194 246 194 196 368 634 368 495 368 300 257]]
                   :right-ascension 0.8076666666666666
                   :declination 37.43183333333333
                   :area 722.278})

;; schema

(def schema #:constellation{:abbreviation {:db/unique :db.unique/identity}})

;; model

(defn cal-celestial-sphere-position [right-ascension declination]
  (let [radius 1000000]
    (v3/from-spherical-coords
     radius
     (gmath/to-radians (- 90 declination))
     (gmath/to-radians right-ascension))))

;; subs

(defn sub-all-constellation-ids [conn]
  @(p/q '[:find [?id ...]
          :where
          [?id :constellation/abbreviation _]]
        conn))


(defn sub-constellation [conn constel-id]
  (let [constel @(p/pull conn '[*] constel-id)
        star-lines (:constellation/star-lines constel)
        pulled-star-lines (for [star-line star-lines]
                            (for [star-id star-line]
                              @(p/pull conn '[:db/id :star/right-ascension :star/declination] star-id)))]

    (assoc constel :constellation/star-lines pulled-star-lines)))
