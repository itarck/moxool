(ns astronomy.app.scene-in-editor
  (:require
   [integrant.core :as ig]
   [methodology.lib.circuit]
   [astronomy.system.solar :as system.solar]
   [film.system.editor :refer [create-editor-system]]))



(def props-sample 
  #:app {:name "astronomy"
         :db-url "/edn/store-system-conn1.edn"})


(derive :app/scene-system :circuit/system)
(derive :app/editor-system :circuit/system)


(defn create-app! [props]
  (let [{:app/keys [name db-url]} props
        app-config #:app {:scene-system #:system{:system-fn system.solar/create-system!
                                                 :props {}}
                          :editor-system #:system {:system-fn create-editor-system
                                                   :props {:system-name name
                                                           :db-url db-url}
                                                   :env {:scene-system (ig/ref :app/scene-system)}}}
        instance (ig/init app-config)]
    #:app {:view (get-in instance [:app/editor-system :system/view])
           :editor-system (:app/editor-system instance)
           :scene-system (:app/scene-system instance)}))


(defonce editor-app-instance (create-app!
                              #:app {:name "astronomy"}))
