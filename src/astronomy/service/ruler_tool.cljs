(ns astronomy.service.ruler-tool
  (:require
   [astronomy.model.astronomical-point :as m.apt]
   [astronomy.model.astro-scene :as m.astro-scene]
   [astronomy.model.user.ruler-tool :as m.ruler-tool]
   [astronomy.service.effect :as s.effect :refer [create-effect]]))


;; handle-event version

(defmulti handle-event (fn [props env event] (:event/action event)))

(defmethod handle-event :ruler-tool/log
  [_props _env {:event/keys [detail]}]
  (create-effect :log detail))


(defmethod handle-event :ruler-tool/mouse-clicked
  [{:keys [astro-scene] :as props} {:keys [db]} {:event/keys [detail]}]
  (let [{:keys [meta-key current-tool mouse-direction]} detail]
    (when meta-key
      (let [scene-coordinate (m.astro-scene/pull-scene-coordinate db astro-scene)
            camera (m.astro-scene/pull-scene-camera db astro-scene)
            apt-1 (m.apt/from-local-camera-view scene-coordinate
                                                (:camera/positon camera) mouse-direction)]

        (case (:ruler-tool/status current-tool)
          :init (create-effect :tx (m.ruler-tool/change-select1-tx current-tool apt-1))
          :select1 (create-effect :tx (m.ruler-tool/change-select2-tx current-tool apt-1))
          :select2 (create-effect :tx (m.ruler-tool/change-select1-tx current-tool apt-1)))))))

