(ns astronomy.model.constellation
  (:require
   [datascript.core :as d]
   [shu.three.vector3 :as v3]
   [shu.goog.math :as gmath]
   [shu.astronomy.light :as shu.light]
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

(def schema #:constellation{:abbreviation {:db/unique :db.unique/identity}
                            :chinese-name {:db/unique :db.unique/identity}})


;; model

(def find-all-ids-query
  '[:find [?id ...]
    :where
    [?id :constellation/abbreviation _]])

(def find-all-names-query
  '[:find [?name ...]
    :where
    [?id :constellation/chinese-name ?name]])

(defn find-all-star-ids [db]
  (let [constels (d/q '[:find [?star-lines ...]
                        :where [?id :constellation/star-lines ?star-lines]]
                      db)]
    (distinct (flatten constels))))

(def find-all-group-names-query
  '[:find [?group-name ...]
    :where
    [?id :constellation/group ?group-name]])


;; subs

(defn sub-all-constellation-ids [conn]
  @(p/q '[:find [?id ...]
          :where
          [?id :constellation/abbreviation _]]
        conn))

(defn sub-all-constellation-star-ids [conn]
  (let [constels @(p/q '[:find [?star-lines ...]
                        :where [?id :constellation/star-lines ?star-lines]]
                      conn)]
    (distinct (flatten constels))))

(defn sub-all-constellation-stars [conn]
  (let [ids (sub-all-constellation-star-ids conn)]
    (doall
     (mapv (fn [id] @(p/pull conn '[*] id)) ids))))

(defn sub-constellation [conn constel-id]
  (let [constel @(p/pull conn '[*] constel-id)
        star-lines (:constellation/star-lines constel)
        pulled-star-lines (for [star-line star-lines]
                            (for [star-id star-line]
                              @(p/pull conn '[:db/id :star/right-ascension :star/declination] star-id)))]

    (assoc constel :constellation/star-lines pulled-star-lines)))


(defn sub-all-constellations-names [conn]
  (let [names @(p/q find-all-names-query conn)]
    (sort names)))

(defn sub-all-group-names [conn]
  @(p/q find-all-group-names-query conn))


(defn sub-ids-by-group-name [conn group-name]
  @(p/q '[:find [?id ...]
          :in $ ?group-name
          :where
          [?id :constellation/group ?group-name]]
        conn group-name))

;; model

(defn cal-celestial-sphere-position [right-ascension declination]
  (let [radius (* 1 shu.light/light-year-unit)]
    (v3/from-spherical-coords
     radius
     (gmath/to-radians (- 90 declination))
     (gmath/to-radians right-ascension))))

;; tx

(defn update-show-lines-tx [constel show?]
  [{:db/id (:db/id constel)
    :constellation/show-lines? show?}])

(defn update-show-name-tx [constel show?]
  [{:db/id (:db/id constel)
    :constellation/show-name? show?}])