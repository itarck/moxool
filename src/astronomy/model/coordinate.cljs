(ns astronomy.model.coordinate
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [shu.three.vector3 :as v3]
   [methodology.model.object :as m.object]))


(def schema {:coordinate/name {:db/unique :db.unique/identity}})

(comment
  (def sample
    #:coordinate {:name "赤道天球坐标系"
                  :object/position [0 0 0]
                  :object/quaternion [0 0 0 1]
                  :type :astronomical-coordinate})
  
  )

;; transform 

(defn from-system-vector
  "把系统坐标系的点转化到当前坐标系"
  [coordinate system-vector]
  (let [matrix (m.object/cal-invert-matrix coordinate)
        pt (v3/from-seq system-vector)]
    (vec (v3/apply-matrix4 pt matrix))))

(defn to-system-vector
  "把系统坐标系的点转化到当前坐标系"
  [coordinate local-vector]
  (let [matrix (m.object/cal-matrix coordinate)
        pt (v3/from-seq local-vector)]
    (vec (v3/apply-matrix4 pt matrix))))

(def cal-invert-matrix m.object/cal-invert-matrix)

;; find 

(defn find-all-ids [db]
  (d/q '[:find [?id ...]
         :where [?id :coordinate/name _]]
        db))

;; sub

(defn sub-all-coordinate-names [conn]
  @(p/q '[:find [?name ...]
          :where [?id :coordinate/name ?name]]
        conn))

(defn sub-scene-coordinate [conn scene]
  (let [full-scene @(p/pull conn '[*] (:db/id scene))
        coordinate @(p/pull conn '[*] (get-in full-scene [:astro-scene/coordinate :db/id]))]
    coordinate))

