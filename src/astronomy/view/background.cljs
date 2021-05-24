(ns astronomy.view.background
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :as a :refer [go >! <!]]
   [helix.core :refer [defnc $]]
   [helix.hooks :refer [use-memo]]
   [posh.reagent :as p]
   [shu.goog.math :as gmath]
   [shu.three.vector3 :as v3]
   [shu.general.core :as g]
   [astronomy.model.constellation :as m.constel]))


(defn visual-magnitude->size [vm]
  (/ (/ 31536000 80) (Math/pow 1.3 vm)))


(defn StarsProjectionComponent [props {:keys [conn] :as env}]
  [:<>
   (for [star (:stars props)]
     (let [{:star/keys [right-ascension declination visual-magnitude]} star
           position (seq (m.constel/cal-celestial-sphere-position right-ascension declination))]
      ;;  (println star)
       (when (and right-ascension declination visual-magnitude position)
         ($ "points" {:key (:db/id star)}
            ($ "bufferGeometry"
               ($ "bufferAttribute" {:attach-object #js ["attributes" "position"]
                                     :count         1
                                     :array         (js/Float32Array. (clj->js position))
                                     :item-size     3}))
            ($ "pointsMaterial" {:size             (* 100 (visual-magnitude->size visual-magnitude))
                                 :size-attenuation true
                                 :color            "white"
                                 :transparent      true
                                 :opacity          1
                                 :fog              false})))))])



(defnc StarsView [props env]
  #_(println "mount stars")
  (let [{:keys [star-count] :or {star-count 1000}} props
        positions (use-memo [star-count]
                            (let [positions #js []]
                              (doseq [_i (range star-count)]
                                (let [[x y z] (v3/from-spherical-coords
                                               (* (+ 0.5 (* (rand) 0.3)) 5000000000)
                                               (* (rand) Math/PI)
                                               (* (rand) Math/PI (g/rand-sign)))]
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
       ($ "pointsMaterial" {:size             1200000000
                            :size-attenuation true
                            :color            "white"
                            :transparent      true
                            :opacity          1
                            :fog              false}))))


(def low-distance (* 100000000 365 86400))
(def up-distance (* 9000000000 365 86400))


(defnc StarsView2 [props]
  #_(println "mount stars")
  (let [{:keys [star-count] :or {star-count 1000}} props
        positions (use-memo [star-count]
                            (let [positions #js []]
                              (doseq [_i (range star-count)]
                                (let [[x y z] (v3/from-spherical-coords
                                               (* (+ 0.5 (* (rand) 0.3)) low-distance)
                                               (* (rand) Math/PI)
                                               (* (rand) Math/PI (g/rand-sign)))]
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
       ($ "pointsMaterial" {:size             900000000000000
                            :size-attenuation true
                            :color            "orange"
                            :transparent      true
                            :opacity          1
                            :fog              false}))))


(defnc StarsView3 [props]
  #_(println "mount stars")
  (let [{:keys [star-count] :or {star-count 1000}} props
        positions (use-memo [star-count]
                            (let [positions #js []]
                              (doseq [_i (range star-count)]
                                (let [[x y z] (v3/from-spherical-coords
                                               (* (+ 0.5 (* (rand) 0.3)) up-distance)
                                               (* (rand) Math/PI)
                                               (* (rand) Math/PI (g/rand-sign)))]
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
       ($ "pointsMaterial" {:size             60000000000000000
                            :size-attenuation true
                            :color            "red"
                            :transparent      true
                            :opacity          1
                            :fog              false}))))

(defn BackgroundView [props env]
  [:<>
   ($ StarsView)
   ($ StarsView2)
   ($ StarsView3)])
