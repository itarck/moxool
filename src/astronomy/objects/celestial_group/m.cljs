(ns astronomy.objects.celestial-group.m
  (:require
   [cljs.spec.alpha :as s]
   [datascript.core :as d]
   [posh.reagent :as p]
   [shu.three.vector3 :as v3]
   [astronomy.objects.celestial.m :as m.celestial]))


;; sample

(def cg-1
  #:celestial-group
   {:object/position [0 0 100]
    :object/quaternion [0 0 0 1]
    :celestial/clock [:clock/name "default"]
    :celestial/_group [{:db/id [:planet/name "earth"]} {:db/id [:satellite/name "moon"]}]
    :entity/chinese-name "地月系"
    :entity/type :celestial-group})


;; schema


;; transform

(defn cal-system-position-now
  "在系统参考系里的位置"
  [db cg-1]
  (:object/position cg-1))

;; query

(def query-all-id-and-chinese-name
  '[:find ?id ?chinese-name
    :where
    [?id :entity/type :celestial-group]
    [?id :entity/chinese-name ?chinese-name]])

;; find

(defn find-all [db]
  (let [ids (d/q '[:find [?id ...]
                   :where [?id :entity/type :celestial-group]]
                 db)]
    (map (fn [id] {:db/id id}) ids)))

;; tx 

(defn update-current-position-tx
  [db celestial-group]
  {:pre [(s/assert :methodology/entity celestial-group)]}
  (let [celestials (d/pull db '[{:celestial/_group [*]}] (:db/id celestial-group))
        positions (map (fn [c] (v3/multiply-scalar (v3/from-seq (m.celestial/cal-system-position-now db c))
                                                   (:celestial/mass c))) (:celestial/_group celestials))
        total-mass (reduce + (map (fn [c] (:celestial/mass c)) (:celestial/_group celestials)))
        mean-position (v3/multiply-scalar (reduce v3/add positions) (/ 1. total-mass))]
    [{:db/id (:db/id celestial-group)
      :object/position (seq mean-position)}]))


;; multi

(defmethod m.celestial/cal-system-position-now :celestial-group
  [db celestial-group]
  (cal-system-position-now db celestial-group))