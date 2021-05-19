(ns astronomy.system.scene
  (:require
   [integrant.core :as ig]
   [methodology.lib.circuit]
   [astronomy.service.meta :refer [init-meta-service!]]
   [astronomy.service.core :refer [init-service-center!]]
   [astronomy.view.core :refer [RootView]]
   [astronomy.model.core :refer [basic-db]]))


(derive ::conn :circuit/conn)
(derive ::dom-atom :circuit/atom)
(derive ::view :circuit/view)
(derive ::chan :circuit/chan)
(derive ::service :circuit/service)

(derive ::meta-atom :circuit/ratom)
(derive ::meta-chan :circuit/chan)
(derive ::meta-service :circuit/service)



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
                                     :meta-atom (ig/ref ::meta-atom)
                                     :dom-atom (ig/ref ::dom-atom)}}
                ::service #:service {:service-fn init-service-center!
                                     :props {:astro-scene {:db/id [:scene/name "solar"]}
                                             :camera {:db/id [:camera/name "default"]}
                                             :user {:db/id [:person/name "dr who"]}}
                                     :env {:service-chan (ig/ref ::chan)
                                           :conn (ig/ref ::conn)
                                           :meta-atom (ig/ref ::meta-atom)
                                           :dom-atom (ig/ref ::dom-atom)}}
                ::meta-chan #:chan {}
                ::meta-atom #:ratom {:init-value {:mode :read-and-write}}
                ::meta-service #:service{:service-fn init-meta-service!
                                          :props {}
                                          :env {:meta-chan (ig/ref ::meta-chan)
                                                :meta-atom (ig/ref ::meta-atom)
                                                :service-chan (ig/ref ::chan)
                                                :conn (ig/ref ::conn)}}}
        instance (ig/init config)]

    #:system {:conn (::conn instance)
              :view (::view instance)
              :service-chan (::chan instance)
              :dom-atom (::dom-atom instance)
              :meta-atom (::meta-atom instance)
              :meta-chan (::meta-chan instance)}))

