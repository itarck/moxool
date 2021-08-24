(ns astronomy.objects.horizon-coordinate.m
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [shu.geometry.angle :as angle]
   [shu.three.quaternion :as q]
   [shu.three.vector3 :as v3]
   [shu.three.spherical :as sph]
   [shu.three.matrix4 :as mat4]
   [shu.astronomy.celestial-coordinate :as shu.cc]
   [astronomy.model.celestial :as m.celestial]
   [astronomy.model.coordinate :as m.coordinate]))


(def schema
  #:horizontal-coordinate {:name {:db/unique :db.unique/identity}
                           :chinese-name {:db/unique :db.unique/identity}})


(defn horizon-coordinate? [coordinate]
  (= (:coordinate/type coordinate) :horizon-coordinate))


(defn cal-local-time [hc epoch-time]
  (let [{:horizon-coordinate/keys [longitude]} hc]
    (+ epoch-time (/ longitude 360))))

(defn get-sun-position [hc]
  (m.coordinate/from-system-position-now hc [0 0 0]))

(defn sun-elevation-angle
  [sun-position]
   (let [phi (angle/to-degrees (v3/angle-to (v3/from-seq [0 1 0]) (v3/from-seq sun-position)))]
     (- 90 phi)))



(comment

  (def sample
    #:horizon-coordinate{:center-object [:planet/name "earth"]
                         :center-radius 0.02
                         :longitude 0
                         :latitude 30

                         :object/scene [:scene/name "solar"]
                         :entity/type :horizon-coordinate})

  (def horizon-coordinate-1
    #:horizon-coordinate{:db/id -1004
                         :entity/type :horizon-coordinate
                         :center-object [:planet/name "earth"]
                         :center-radius 0.0211
                         :radius 0.002
                         :longitude 0
                         :latitude 0
                         :coordinate/name "地平坐标系"
                         :coordinate/type :horizon-coordinate
                         :object/scene [:scene/name "solar"]})


;;   
  )


;; transform 

(defn cal-local-position [hc]
  (let [{:horizon-coordinate/keys [longitude latitude center-radius longitude-0-offset]} hc
        cc (shu.cc/celestial-coordinate (+ longitude-0-offset longitude) latitude center-radius)]
    (shu.cc/cal-position cc)))


(defn cal-local-quaternion [hc]
  (let [sp (apply sph/from-cartesian-coords (cal-local-position hc))
        phi-axis (v3/normalize (v3/cross (v3/vector3 0 1 0) (v3/normalize (cal-local-position hc))))
        q1 (q/from-axis-angle phi-axis (:phi sp))
        q2 (q/from-axis-angle (v3/vector3 0 1 0) (:theta sp))]
    (q/multiply q1 q2)))

;; query and pull

(def query-horizon-coordinate-names
  '[:find [?name ...]
    :where
    [?id :coordinate/name ?name]
    [?id :entity/type :horizon-coordinate]]
  )

(defn find-horizon-coordinate-names [db]
  (d/q query-horizon-coordinate-names db))

(defn pull-one-by-name [db name]
  (d/pull db '[*] [:coordinate/name name]))

;; tx

(defn change-show-latitude-tx [hc-nw show]
  [{:db/id (:db/id hc-nw)
    :horizon-coordinate/show-latitude? show}])

(defn change-show-longitude-tx [hc-nw show]
  [{:db/id (:db/id hc-nw)
    :horizon-coordinate/show-longitude? show}])

(defn change-show-compass-tx [hc show?]
  [{:db/id (:db/id hc)
    :horizon-coordinate/show-compass? show?}])

(defn change-show-horizontal-plane-tx [hc show?]
  [{:db/id (:db/id hc)
    :horizon-coordinate/show-horizontal-plane? show?}])

(defn change-radius-tx [hc radius]
  [{:db/id (:db/id hc)
    :horizon-coordinate/radius radius}])

(defn update-position-and-quaternion-tx [db horizon-coordinate]
  (let [hc (d/pull db '[* {:horizon-coordinate/center-object [*]}] (:db/id horizon-coordinate))
        center-object (:horizon-coordinate/center-object hc)
        center-position (m.celestial/cal-system-position-now db center-object)
        center-quaternion (get-in center-object [:object/quaternion])
        s1 (v3/vector3 1 1 1)
        center-matrix (mat4/compose (v3/from-seq center-position) (q/from-seq center-quaternion) s1)
        local-position (cal-local-position hc)
        local-quaternion (cal-local-quaternion hc)
        local-matrix (mat4/compose local-position local-quaternion s1)
        world-matrix (mat4/multiply center-matrix local-matrix)
        [p3 q3 s3] (mat4/decompose world-matrix)]
    [{:db/id (:db/id hc)
      :coordinate/center-position center-position
      :coordinate/center-quaternion center-quaternion
      :object/position (seq p3)
      :object/quaternion (seq q3)}]))


;; sub views

(defn sub-horizon-coordinate-names [conn]
  @(p/q query-horizon-coordinate-names conn))


;; 实现接口

(defmethod m.coordinate/update-position-and-quaternion-tx
  :horizon-coordinate
  [db hc]
  (update-position-and-quaternion-tx db hc))