(ns astronomy.app.core
  ;; (:require-macros [methodology.lib.resource])
  (:require
   [applied-science.js-interop :as j]
   [reagent.dom :as rdom]
  ;;  [astronomy.system.mini :as mini]
   [astronomy.system.solar2 :as solar2]
   ))


;; mount point

(defn update! []
  (rdom/render
  ;;  (:astronomy/root-view mini/app)
   (:astronomy/root-view solar2/app)
   (j/call js/document :getElementById  "app")))


(defn ^:export init! []
  (update!))



