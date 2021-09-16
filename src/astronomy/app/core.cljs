(ns astronomy.app.core
  ;; (:require-macros [methodology.lib.resource])
  (:require
   [applied-science.js-interop :as j]
   [reagent.dom :as rdom]
   [astronomy.system.mini :as mini]
   [film2.system.studio :as studio]
   [astronomy.conn.mini-factory :as mini-factory]
  ;;  [astronomy.system.solar2 :as solar2]
   ))


;; mount point

;; (def system (studio/create-app! {}))

(def ioframe-config1
  (let [db (mini-factory/create-db1)]
    #:ioframe {:db db
               :name "mini-1"
               :type "mini"
               :description "只有太阳和地球的小型系统"}))


(def ioframe-config2
  #:ioframe {:db-url "/temp/frame/solar-1.fra"
             :name "mini-1"
             :type "mini"
             :description "只有太阳和地球的小型系统"})

(def ioframe-system (mini/create-ioframe-system ioframe-config2))


(defn update! []
  (rdom/render
  ;;  (:astronomy/root-view mini/app)
  ;;  (:studio/view system)
   (:ioframe-system/view ioframe-system)
  ;;  (:astronomy/root-view solar2/app)
   (j/call js/document :getElementById  "app")))


(defn ^:export init! []
  (update!))



