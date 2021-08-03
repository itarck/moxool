(ns astronomy.component.mouse
  (:require 
   [applied-science.js-interop :as j]
   [shu.three.vector3 :as v3]))


(defn get-normalized-mouse [three-obj]
  (let [x (j/get-in three-obj [:mouse :x])
        y (j/get-in three-obj [:mouse :y])]
    [x y]))

(defn get-camere-object [three-obj]
  (j/get three-obj :camera))

(defn to-world-vector3 [three-obj normalized-mouse]
  (let [[x y] normalized-mouse
        camera (get-camere-object three-obj)
        v3-1 (v3/vector3 x y -1)]
    (j/call v3-1 :unproject camera)))

(defn get-camera-position [three-obj]
  (j/get-in three-obj [:camera :position]))


(defn get-mouse-direction-vector3 [three-obj]
  (let [nmouse (get-normalized-mouse three-obj)
        world-v3 (to-world-vector3 three-obj nmouse)
        camera-position (get-camera-position three-obj)
        direction (v3/normalize (v3/sub world-v3 camera-position))]
    direction))
