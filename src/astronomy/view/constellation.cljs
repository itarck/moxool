(ns astronomy.view.constellation
  (:require
   [applied-science.js-interop :as j]
   [shu.goog.math :as gmath]
   [shu.three.vector3 :as v3]
   ["@react-three/drei" :refer [Html]]
   ["three" :as three]
   [astronomy.model.constellation :as m.constel]))


(defn gen-star-points [stars]
  (let [radius 31536000]
    (clj->js
     (for [star stars]
       (let [{:star/keys [right-ascension declination]} star
             pt (v3/from-spherical-coords
                 radius
                 (gmath/to-radians (- 90 declination))
                 (gmath/to-radians right-ascension))]
         pt)))))


(defn StarLineView [star-line]
  (let [lineGeometry (three/BufferGeometry.)]
    (j/call lineGeometry :setFromPoints (gen-star-points star-line))
    [:line {:geometry lineGeometry}
       [:lineBasicMaterial {:args {:linewidth 1
                                   :color "green"
                                   :linecap "butt"
                                   :linejoin "butt"}}]]))


(defn ConstellationView [props {:keys [conn] :as env}]
  (let [{:keys [constellation]} props
        constel-entity (m.constel/sub-constellation conn (:db/id constellation))
        {:constellation/keys [chinese-name right-ascension declination star-lines]} constel-entity]
    [:<>
     [:> Html {:position (vec (m.constel/cal-celestial-sphere-position right-ascension declination))
               :zIndexRange [0 0]
               :style {:color "green"
                       :font-size "14px"}}
      [:p chinese-name]]
     (for [star-line star-lines]
       ^{:key (str (:db/id constel-entity) (rand))}
       [StarLineView star-line])]))


(defn ConstellationsView [props {:keys [conn] :as env}]
  (let [constellation-ids (m.constel/sub-all-constellation-ids conn)]
    [:<>
     (for [id constellation-ids]
       ^{:key id}
       [ConstellationView {:constellation {:db/id id}} env])])
  
  )