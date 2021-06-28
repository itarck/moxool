(ns film.system.player
  (:require
   [integrant.core :as ig]
   [methodology.lib.circuit]
   [film.service.core :refer [init-service-center!]]
   [film.view.player :refer [RootView]]
   [film.model.core :refer [schema basic-db]]))



(derive ::chan :circuit/chan)
(derive ::conn :circuit/conn)
(derive ::service :circuit/service)
(derive ::view :circuit/view)



(defn create-player-system [props env]
  (let [{:keys [system-name db-url]} props
        {:keys [scene-system]} env
        config {::chan #:chan{}
                ::conn #:conn{:schema schema
                              :initial-db basic-db
                              :db-url db-url}
                ::service #:service{:service-fn init-service-center!
                                    :props {:player {:db/id [:player/name "default"]}}
                                    :env {:studio-chan (ig/ref ::chan)
                                          :studio-conn (ig/ref ::conn)
                                          :scene-system scene-system}}
                ::view #:view{:view-fn RootView
                              :props {:player-id [:player/name "default"]}
                              :env {:studio-chan (ig/ref ::chan)
                                    :studio-conn (ig/ref ::conn)
                                    :scene-system scene-system}}}
        instance (ig/init config)]
    #:system{:view (::view instance)
             :conn (::conn instance)}))


;; mount point


;; (def system (ig/init studio-config))


;; (defn update! []
;;   (dom/render
;;    (:studio/view system)
;;    (j/call js/document :getElementById  "app")))

;; (defn ^:export init! []
;;   (update!))

