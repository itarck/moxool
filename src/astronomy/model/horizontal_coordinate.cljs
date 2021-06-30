(ns astronomy.model.horizontal-coordinate
  (:require
   [shu.three.quaternion :as q]
   [shu.three.vector3 :as v3]
   [shu.three.spherical :as sph]))


(def schema
  #:horizontal-coordinate {:name {:db/unique :db.unique/identity}})


(comment

  (def sample
    #:horizontal-coordinate{:position [0.02 0.02 0.02]
                            :longitude-interval 90
                            :latitude-interval 10
                            :radius 0.001
                            :show-latitude? true
                            :show-longitude? true
                            :show-horizontal-plane? true
                            :show-compass? true

                            :object/scene [:scene/name "solar"]

                            :entity/type :horizontal-coordinate})

;;   
  )



(defn cal-radius [hc]
  (let [position (:horizontal-coordinate/position hc)]
    (v3/length (v3/from-seq position))))


(defn cal-quaternion [hc]
  (seq (q/from-unit-vectors
        (v3/vector3 0 1 0)
        (v3/normalize (v3/from-seq (:horizontal-coordinate/position hc))))))


(defn cal-spherical-coordinate [hc]
  (let [[x y z] (:horizontal-coordinate/position hc)
        sp (sph/from-cartesian-coords x y z)]
    sp))

(defn cal-phi-rotate-axis [hc]
  (let [pv (v3/from-seq (:horizontal-coordinate/position hc))]
    (v3/normalize (v3/cross (v3/vector3 0 1 0) (v3/normalize pv)))))


(defn cal-quaternion-on-sphere [position]
  (let [hc #:horizontal-coordinate{:position position}
        sp (cal-spherical-coordinate hc)
        q1 (q/from-axis-angle (cal-phi-rotate-axis hc) (:phi sp))
        q2 (q/from-axis-angle (v3/vector3 0 1 0) (:theta sp))]
    (q/multiply q1 q2)))


;; tx


(defn set-position-tx [hc new-position]
  [#:horizontal-coordinate {:position new-position
                            :db/id (:db/id hc)}])



(comment
  (cal-radius sample)
  (cal-quaternion sample)
  ;; => (0.32505758367186816 0 -0.32505758367186816 0.8880738339771153)

  (cal-quaternion-on-sphere [1 1 1])
  ;; => #object[Quaternion [0.42470820027786693 0.33985114297998736 -0.17591989660616117 0.8204732385702833]]
  )