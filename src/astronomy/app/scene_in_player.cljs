(ns astronomy.app.scene-in-player
  (:require
   [integrant.core :as ig]
   [methodology.lib.circuit]
   [astronomy.system.solar :as system.solar]
   [film.system.player :refer [create-player-system]]))


(def props-sample
  #:app {:name "astronomy"
         :db-url "/edn/store-system-conn1.edn"})


(derive :app/scene-system :circuit/system)
(derive :app/player-system :circuit/system)


(defn create-app! [props]
  (let [{:app/keys [name db-url]} props
        app-config #:app {:scene-system #:system{:system-fn system.solar/create-system!
                                                 :props {}}
                          :player-system #:system {:system-fn create-player-system
                                                   :props {:system-name name
                                                           :db-url db-url}
                                                   :env {:scene-system (ig/ref :app/scene-system)}}}
        instance (ig/init app-config)]
    #:app {:view (get-in instance [:app/player-system :system/view])
           :scene-system (get-in instance [:app/scene-system])
           :player-system (get-in instance [:app/player-system])}))
