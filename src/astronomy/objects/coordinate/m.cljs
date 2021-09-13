(ns astronomy.objects.coordinate.m
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
                  :type :astronomical-coordinate}))

;; transform 

;; now

(defmulti cal-origin-position-now
  "原点在系统坐标内的位置"
  (fn [_db coor] (:entity/type coor)))

(def cal-origin-matrix-now
  m.object/cal-matrix)

(def cal-origin-invert-matrix-now
  m.object/cal-invert-matrix)

(defn from-system-position-now
  "把系统坐标系的点转化到当前坐标系"
  [coordinate system-vector]
  (let [matrix (m.object/cal-invert-matrix coordinate)
        pt (v3/from-seq system-vector)]
    (vec (v3/apply-matrix4 pt matrix))))

(defn to-system-position-now
  "把系统坐标系的点转化到当前坐标系"
  [coordinate local-vector]
  (let [matrix (m.object/cal-matrix coordinate)
        pt (v3/from-seq local-vector)]
    (vec (v3/apply-matrix4 pt matrix))))

(defmulti update-position-and-quaternion-tx
  (fn [_db coor] (:entity/type coor)))

;; at epoch days


(defmulti from-system-position-at-epoch
  "从系统坐标转移到坐标系坐标，坐标系随时间变化"
  (fn [_db coordinate _epoch-days _system-position] (:entity/type coordinate)))

(defmulti to-system-position-at-epoch
  "坐标系坐标转换到系统坐标，坐标系在随时间变化"
  (fn [_db coordinate _epoch-days _system-position] (:entity/type coordinate)))


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
