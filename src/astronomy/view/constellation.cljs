(ns astronomy.view.constellation
  (:require
   [applied-science.js-interop :as j]
   [shu.goog.math :as gmath]
   [shu.three.vector3 :as v3]
   ["@react-three/drei" :refer [Html]]
   ["three" :as three]
   [posh.reagent :as p]
   [shu.astronomy.light :as shu.light]
   [astronomy.model.astro-scene :as m.astro-scene]
   [astronomy.model.constellation :as m.constel]))


(defn gen-star-points [stars]
  (let [radius (* 1 shu.light/light-year-unit)]
    (clj->js
     (for [star stars]
       (let [{:star/keys [right-ascension declination]} star
             pt (v3/from-spherical-coords
                 radius
                 (gmath/to-radians (- 90 declination))
                 (gmath/to-radians right-ascension))]
         pt)))))


(defn StarLineView [star-line color]
  (let [lineGeometry (three/BufferGeometry.)]
    (j/call lineGeometry :setFromPoints (gen-star-points star-line))
    [:line {:geometry lineGeometry}
       [:lineBasicMaterial {:args {:linewidth 1
                                   :color color
                                   :linecap "butt"
                                   :linejoin "butt"}}]]))


(defn ConstellationView [props {:keys [conn] :as env}]
  (let [constel-entity (m.constel/sub-constellation conn (get-in props [:object :db/id]))
        {:constellation/keys [show-lines? show-name? chinese-name right-ascension declination star-lines]} constel-entity
        constellation-color (-> constel-entity :constellation/family :constellation-family/color)]
    [:<>
     (when show-name?
       [:> Html {:position (vec (m.constel/cal-celestial-sphere-position right-ascension declination))
                 :zIndexRange [0 0]
                 :style {:color constellation-color
                         :font-size "14px"}}
        [:p chinese-name]])
     (when show-lines?
       [:<>
        (for [star-line star-lines]
          ^{:key (str (:db/id constel-entity) (rand))}
          [StarLineView star-line constellation-color])])]))


(defn ConstellationsView [{:keys [astro-scene] :as props} {:keys [conn] :as env}]
    (let [has-day-light? (m.astro-scene/sub-has-day-light? conn astro-scene)
          constellation-ids (m.constel/sub-all-constellation-ids conn)]
      (when (not has-day-light?)
        [:<>
         (for [id constellation-ids]
           ^{:key id}
           [ConstellationView {:object {:db/id id}} env])])))