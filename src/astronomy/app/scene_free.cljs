(ns astronomy.app.scene-free
  (:require
   [integrant.core :as ig]
   [methodology.lib.circuit]
   [astronomy.system.solar :as system.solar]))


(derive :app/scene-system :circuit/system)


(defn create-app! [props]
  (let [app-config #:app {:scene-system #:system{:system-fn system.solar/create-system!
                                                 :props {}}}
        app-instance (ig/init app-config)]
    #:app {:view (get-in app-instance [:app/scene-system :system/view])
           :scene-conn (get-in app-instance [:app/scene-system :system/conn])}))


