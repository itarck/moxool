(ns astronomy.model.horizon-coordinate
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [shu.three.quaternion :as q]
   [shu.three.vector3 :as v3]
   [shu.three.spherical :as sph]
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

(defn update-position-and-quaternion-tx [db id]
  (let [hc (d/pull db '[* {:horizon-coordinate/center-object [*]}] id)
        center-object (:horizon-coordinate/center-object hc)
        center-position (case (:entity/type center-object)
                          :star (:object/position center-object)
                          :planet (m.planet/cal-world-position db center-object)
                          :satellite (m.satellite/cal-world-position db center-object))
        center-quaternion (get-in center-object [:object/quaternion])
        local-position (cal-local-position hc)
        local-quaternion (cal-local-quaternion hc)]
    [{:db/id (:db/id hc)
      :object/position (seq (v3/add (v3/from-seq center-position) local-position))
      :object/quaternion (seq (q/multiply local-quaternion (q/from-seq center-quaternion)))}]))


(comment
  
  (cal-local-position sample)
  ;; => #object[Vector3 [0 0.5000000000000001 0.8660254037844386]]

  (cal-local-quaternion sample)
  ;; => #object[Quaternion [0.49999999999999994 0 0 0.8660254037844387]]

  )