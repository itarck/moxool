(ns astronomy.view.background
  (:require
   [applied-science.js-interop :as j]
   [helix.core :refer [defnc $]]
   [helix.hooks :refer [use-memo]]
   [shu.arithmetic.number :as shu.number]
   [shu.three.vector3 :as v3]
   [shu.astronomy.light :as shu.light]
   [astronomy.model.astro-scene :as m.astro-scene]))



(defnc StarsView [props env]
  #_(println "mount stars")
  (let [{:keys [star-count] :or {star-count 1000}} props
        positions (use-memo [star-count]
                            (let [positions #js []]
                              (doseq [_i (range star-count)]
                                (let [[x y z] (v3/from-spherical-coords
                                               (* (+ 0.5 (* (rand) 0.3)) 5000000000)
                                               (* (rand) Math/PI)
                                               (* (rand) Math/PI (shu.number/rand-sign)))]
                                  (j/push! positions x)
                                  (j/push! positions y)
                                  (j/push! positions z)))
                              (js/Float32Array.  positions)))]
    ($ "points"
       ($ "bufferGeometry"
          ($ "bufferAttribute" {:attach-object #js ["attributes" "position"]
                                :count         star-count
                                :array         positions
                                :item-size     3}))
       ($ "pointsMaterial" {:size             80000000000
                            :size-attenuation true
                            :color            "white"
                            :transparent      true
                            :opacity          1
                            :fog              false}))))

(def astro-scene-scale 1e4)

(def low-distance (* 10000 10000 shu.light/light-year-unit))
(def up-distance (* 400 10000 10000 shu.light/light-year-unit))

(def point-size-ratio 2e-3)


(defnc StarsView2 [props]
  #_(println "mount stars")
  (let [{:keys [star-count] :or {star-count 1000}} props
        positions (use-memo [star-count]
                            (let [positions #js []]
                              (doseq [_i (range star-count)]
                                (let [[x y z] (v3/from-spherical-coords
                                               (* (+ 0.5 (* (rand) 0.5)) low-distance)
                                               (* (rand) Math/PI)
                                               (* (rand) Math/PI (shu.number/rand-sign)))]
                                  (j/push! positions x)
                                  (j/push! positions y)
                                  (j/push! positions z)))
                              (js/Float32Array.  positions)))]
    ($ "points"
       ($ "bufferGeometry"
          ($ "bufferAttribute" {:attach-object #js ["attributes" "position"]
                                :count         star-count
                                :array         positions
                                :item-size     3}))
       ($ "pointsMaterial" {:size             (* point-size-ratio low-distance astro-scene-scale)
                            :size-attenuation true
                            :color            "orange"
                            :transparent      true
                            :opacity          1
                            :fog              false}))))


(defnc StarsView3 [props]
  #_(println "mount stars")
  (let [{:keys [star-count] :or {star-count 10000}} props
        positions (use-memo [star-count]
                            (let [positions #js []]
                              (doseq [_i (range star-count)]
                                (let [[x y z] (v3/from-spherical-coords
                                               (* (+ 0.5 (* (rand) 0.3)) up-distance)
                                               (* (rand) Math/PI)
                                               (* (rand) Math/PI (shu.number/rand-sign)))]
                                  (j/push! positions x)
                                  (j/push! positions y)
                                  (j/push! positions z)))
                              (js/Float32Array.  positions)))]
    ($ "points"
       ($ "bufferGeometry"
          ($ "bufferAttribute" {:attach-object #js ["attributes" "position"]
                                :count         star-count
                                :array         positions
                                :item-size     3}))
       ($ "pointsMaterial" {:size             (* point-size-ratio up-distance astro-scene-scale)
                            :size-attenuation true
                            :color            "red"
                            :transparent      true
                            :opacity          1
                            :fog              false}))))


(defn BackgroundView [_ {:keys [conn]}]
  [:<>
   ($ StarsView)
  ;;  ($ StarsView2)
   #_($ StarsView3)])
