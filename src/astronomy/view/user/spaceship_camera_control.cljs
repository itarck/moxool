(ns astronomy.view.user.spaceship-camera-control
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >!]]
   [cljs-bean.core :refer [bean ->clj ->js]]
   [helix.core :refer [defnc $]]
   [posh.reagent :as p]
   ["three" :as three]
   ["react" :as react :refer [useRef useEffect]]
   ["camera-controls" :as CameraControls]
   ["react-three-fiber" :refer [useFrame extend useThree]]))


(extend #js {:CameraControls CameraControls})


(def spaceship-camera-control-entity
  #:spaceship-camera-control
   {:name "default"
    :position [2000 2000 2000]
    :up [0 1 0]
    :target [0 0 0]
    :min-distance 10000
    :tool/name "spaceship camera tool"
    :tool/chinese-name "相机控制"
    :tool/icon "/image/pirate/cow.jpg"
    :entity/type :spaceship-camera-control})


(defnc CameraControlsComponent [props]
  (let [{:keys [position up target minDistance maxDistance domAtom]} props
        [px py pz] (seq position)
        [tx ty tz] (seq target)
        [ux uy uz] (seq up)
        {:keys [gl camera]} (bean (useThree))
        ref (useRef)]
    (j/call CameraControls :install #js {:THREE three})
    (useFrame (fn [_state delta]
                (when (j/get ref :current)
                  (j/call-in ref [:current :update] delta))))
    (useEffect (fn []
                 (swap! domAtom assoc :spaceship-camera-control (j/get ref :current))
                 (j/call-in camera [:up :set] ux uy uz)
                 (j/call-in ref [:current :updateCameraUp])
                 (j/call-in ref [:current :setLookAt] px py pz tx ty tz true)))
    ($ :cameraControls {:ref ref
                        :args #js [camera (j/get gl :domElement)]
                        :minDistance minDistance
                        :maxDistance (or maxDistance js/Infinity)})))


(defn SpaceshipCameraControlView [props {:keys [conn dom-atom] :as env}]
  (let [camera-control @(p/pull conn '[*] (:db/id props))
        {:spaceship-camera-control/keys [mode position up target min-distance]} camera-control
        orbit-props {:up (->js up)
                     :target (->js target)
                     :position (->js position)
                     :minDistance (* 1.01 min-distance)}
        surface-props {:up (->js up)
                       :target (->js target)
                       :position (->js position)
                       :minDistance 1e-3
                       :maxDistance 1e-3}]
;;     (println "!!camera control: " camera-control)
    (if (= mode :surface-control)
      ($ CameraControlsComponent {:azimuthRotateSpeed -0.3
                                  :polarRotateSpeed -0.3
                                  :domAtom dom-atom
                                  :& surface-props})
      ($ CameraControlsComponent {:azimuthRotateSpeed -0.3
                                  :polarRotateSpeed -0.3
                                  :domAtom dom-atom
                                  :& orbit-props}))))


;; tool view

(def camera-cell
  {:width "42px"
   :height "42px"
   :margin-right "3px"})

(def camera-button
  {:margin "0px"
   :width "40px"
   :height "40px"
   :background "rgba(200, 200, 200, 0.7)"})


(defn SpaceshipCameraToolView [{:keys [camera-control] :as props} {:keys [service-chan conn]}]
  (let [entity @(p/pull conn '[*] (:db/id camera-control))
        mode (:spaceship-camera-control/mode entity)]
    [:div {:style {:position :absolute
                   :bottom "10px"
                   :left "1%"
                   :width "140px"
                   :font-size "24px"}}
     [:div {:class "d-flex justify-content-center"
            :style {:margin "2px"}}
      [:div {:style camera-cell}]
      [:div {:style camera-cell}
       [:div {:style camera-button
              :class "d-flex justify-content-center"}
        [:i {:class "bi bi-caret-up-fill"
             :onClick #(go (>! service-chan #:event{:action :spaceship-camera-control/up}))}]]]
      [:div {:style camera-cell}]]

     [:div {:class "d-flex justify-content-center"
            :style {:margin "2px"}}
      [:div {:style camera-cell}
       [:div {:style camera-button
              :class "d-flex justify-content-center"}
        [:i {:class "bi bi-caret-left-fill"
             :onClick #(go (>! service-chan #:event{:action :spaceship-camera-control/left}))}]]]

      [:div {:style camera-cell}
       [:div {:style camera-button
              :class "d-flex justify-content-center"}
        (if (= mode :orbit-control)
          [:i {:class "bi bi-geo-fill"
               :onClick #(go (>! service-chan #:event{:action :spaceship-camera-control/landing}))}]
          [:i {:class "bi bi-hurricane"
               :onClick #(go (>! service-chan #:event{:action :spaceship-camera-control/fly}))}])]]

      [:div {:style camera-cell}
       [:div {:style camera-button
              :class "d-flex justify-content-center"}
        [:i {:class "bi bi-caret-right-fill"
             :onClick #(go (>! service-chan #:event{:action :spaceship-camera-control/right}))}]]]]

     [:div {:class "d-flex justify-content-center"
            :style {:margin "2px"}}
      [:div {:style camera-cell}]
      [:div {:style camera-cell}
       [:div {:style camera-button
              :class "d-flex justify-content-center"}
        [:i {:class "bi bi-caret-down-fill"
             :onClick #(go (>! service-chan #:event{:action :spaceship-camera-control/down}))}]]]
      [:div {:style camera-cell}]]])
  )