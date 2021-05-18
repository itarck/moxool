(ns astronomy.system.scene
  (:require
   [integrant.core :as ig]
   [methodology.lib.circuit]
   [astronomy.service.scene :refer [init-scene-service!]]
   [astronomy.service.core :refer [init-service-center!]]
   [astronomy.view.core :refer [RootView]]
   [astronomy.model.core :refer [basic-db]]))


(derive ::conn :circuit/conn)
(derive ::dom-atom :circuit/atom)
(derive ::view :circuit/view)
(derive ::chan :circuit/chan)
(derive ::service :circuit/service)

(derive ::scene-atom :circuit/ratom)
(derive ::scene-chan :circuit/chan)
(derive ::scene-service :circuit/service)



(defn create-system! []
  (let [config {::conn #:conn {:initial-db basic-db
                               :db-url "/edn/free-mode.edn"}
                ::dom-atom #:atom {}
                ::chan #:chan {}
                ::view #:view {:view-fn RootView
                               :props {:astro-scene {:db/id [:scene/name "solar"]}
                                       :camera {:db/id [:camera/name "default"]}
                                       :camera-control {:db/id [:spaceship-camera-control/name "default"]}
                                       :user {:db/id [:person/name "dr who"]}}
                               :env {:conn (ig/ref ::conn)
                                     :service-chan (ig/ref ::chan)
                                     :scene-atom (ig/ref ::scene-atom)
                                     :dom-atom (ig/ref ::dom-atom)}}
                ::service #:service {:service-fn init-service-center!
                                     :props {:astro-scene {:db/id [:scene/name "solar"]}
                                             :camera {:db/id [:camera/name "default"]}
                                             :user {:db/id [:person/name "dr who"]}}
                                     :env {:service-chan (ig/ref ::chan)
                                           :conn (ig/ref ::conn)
                                           :scene-atom (ig/ref ::scene-atom)
                                           :dom-atom (ig/ref ::dom-atom)}}
                ::scene-chan #:chan {}
                ::scene-atom #:ratom {:init-value {:mode :read-and-write}}
                ::scene-service #:service{:service-fn init-scene-service!
                                          :props {}
                                          :env {:scene-chan (ig/ref ::scene-chan)
                                                :scene-atom (ig/ref ::scene-atom)
                                                :service-chan (ig/ref ::chan)
                                                :conn (ig/ref ::conn)}}}
        instance (ig/init config)]

    #:system {:conn (::conn instance)
              :view (::view instance)
              :service-chan (::chan instance)
              :dom-atom (::dom-atom instance)
              :scene-atom (::scene-atom instance)
              :scene-chan (::scene-chan instance)}))

