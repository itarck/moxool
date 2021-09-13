(ns astronomy.objects.astronomical-point.v
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   ["react" :as react :refer [Suspense]]
   [astronomy.objects.astronomical-point.m :as m.apt]
   [astronomy.component.cross-hair :as c.cross-hair]))


(defn AstronimicalPointSceneView
  [{:keys [astronomical-point]} {:keys [service-chan]}]
  (let [{:astronomical-point/keys [size] :or {size 1}} astronomical-point]
    [:> Suspense {:fallback nil}
     [:> c.cross-hair/CrossHairComponent
      {:position (vec (m.apt/cal-position-vector3 astronomical-point))
       :size size
       :onClick (fn [e] (go (>! service-chan
                                #:event {:action :user/object-clicked
                                         :detail {:astronomical-point astronomical-point
                                                  :meta-key (j/get-in e [:metaKey])}})))}]]))