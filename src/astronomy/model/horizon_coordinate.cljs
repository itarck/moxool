(ns astronomy.model.horizon-coordinate
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [shu.three.quaternion :as q]
   [shu.three.vector3 :as v3]
   [shu.three.spherical :as sph]
   [shu.three.matrix4 :as mat4]
   [shu.astronomy.celestial-coordinate :as shu.cc]
   [astronomy.model.planet :as m.planet]
   [astronomy.model.satellite :as m.satellite]))


(def schema
  #:horizontal-coordinate {:name {:db/unique :db.unique/identity}
                           :chinese-name {:db/unique :db.unique/identity}})


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
  (let [{:horizon-coordinate/keys [longitude latitude center-radius]} hc
        cc (shu.cc/celestial-coordinate longitude latitude center-radius)]
    (shu.cc/cal-position cc)))


(defn cal-local-quaternion [hc]
  (let [sp (apply sph/from-cartesian-coords (cal-local-position hc))
        phi-axis (v3/normalize (v3/cross (v3/vector3 0 1 0) (v3/normalize (cal-local-position hc))))
        q1 (q/from-axis-angle phi-axis (:phi sp))
        q2 (q/from-axis-angle (v3/vector3 0 1 0) (:theta sp))]
    (q/multiply q1 q2)))

;; tx

(defn set-latitude-tx [hc-nw latitude]
  [{:db/id (:db/id hc-nw)
    :horizon-coordinate/latitude latitude}])

(defn set-longitude-tx [hc-nw longitude]
  [{:db/id (:db/id hc-nw)
    :horizon-coordinate/longitude longitude}])

(defn update-position-and-quaternion-tx [db id]
  (let [hc (d/pull db '[* {:horizon-coordinate/center-object [*]}] id)
        center-object (:horizon-coordinate/center-object hc)
        center-position (case (:entity/type center-object)
                          :star (:object/position center-object)
                          :planet (m.planet/cal-world-position db center-object)
                          :satellite (m.satellite/cal-world-position db center-object))
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

(defn sub-ids-by-query [conn query-type query-args]
  
  )



(comment
  
  (cal-local-position sample)
  ;; => #object[Vector3 [0 0.5000000000000001 0.8660254037844386]]

  (cal-local-quaternion sample)
  ;; => #object[Quaternion [0.49999999999999994 0 0 0.8660254037844387]]

  )