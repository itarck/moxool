(ns astronomy.app.scene-free
  (:require
   [cljs.core.async :refer [go >!]]
   [integrant.core :as ig]
   [methodology.lib.circuit]
   [astronomy.system.solar :as system.solar]))


(derive :app/scene-system :circuit/system)


(defn WrappedFreeView [scene]
  [:div {:style {:height "100%"
                 :width "100%"}}
   scene])

(defn WrappedFreeView2 [scene]
  [:div {:style {:position :absolute
                 :height "720px"
                 :width "1280px"}}
   scene])


(defn create-app! [props]
  (let [{:app/keys [scene-db-url]} props
        app-config #:app {:scene-system #:system{:system-fn system.solar/create-system!
                                                 :props {:db-url scene-db-url}}}
        app-instance (ig/init app-config)]
    #:app {:view [WrappedFreeView (get-in app-instance [:app/scene-system :system/view])] 
           :scene-conn (get-in app-instance [:app/scene-system :system/conn])
           :service-chan (get-in app-instance [:app/scene-system :system/service-chan])
           :scene-system (get-in app-instance [:app/scene-system])}))


(defonce free-app-instance (create-app! #:app{:scene-db-url "/temp/free-mode.edn"}))