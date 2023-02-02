(ns astronomy.app.extension
  (:require
   [reagent.dom :as rdom]
   [promesa.core :as p]
   [reagent.core :as r]
   [datascript.core :as d]
   [datascript.transit :as dt]
   [integrant.core :as ig]
   [fancoil2.core :as fc]
   [astronomy.system.solar :as solar]
   )
  )


(def global-atom
  (r/atom {}))


(def user-plugins
  {:routes [["/" {:name :app/root-view
                  :on-load :app/init!}]]

   :view {:app/root-view
          (fn [{:keys [self]} _ {:keys [path]}]
            (let [system (:system @global-atom)]
              (if system
                (:astronomy/root-view system)
                [:div "loading..."])))}

   :process {:app/init!
             (fn [{:keys [fx pconn]} _ {:keys [path] :as props}]
               (let [db-sync (fn [tx-report]
                               (let [{:keys [db-after]} tx-report
                                     transit-str (dt/write-transit-str db-after)]
                                 (fx :fx.ajax/post
                                      {:url "http://localhost:3003/api/fs/file/write"
                                       :params {:path path
                                                :content transit-str}})))]
                 (when path
                   (p/let [content (fx :fx.ajax/post {:url "http://localhost:3003/api/fs/file/read"
                                                      :params {:path path}})
                           db (dt/read-transit-str content)]
                     (swap! global-atom assoc :db db)
                     (let [astro-config {:astronomy/conn {:initial-db db}}
                           astro-system (solar/create-system! astro-config)]
                       (swap! global-atom assoc :system astro-system))
                     
                     #_(fx :fx.posh/reset-conn-from-transit-str {:transit-str content})
                     #_(fx :fx.posh/listen {:key :db-sysnc
                                            :callback db-sync})))))}})



(def hierarchy
  {;;  user field
   ::pconn [:fancoil2/pconn]
   ::ratom [:fancoil2/ratom]
   ::cache [:fancoil2/ratom]
   ::model [:fancoil2/model]
   ::fx [:fancoil2/fx]
   ::handle [:fancoil2/handle]
   ::process [:fancoil2/process]
   ::subscribe [:fancoil2/subscribe]
   ::view [:fancoil2/view]
   ::chan [:fancoil2/chan]
   ::dispatch [:fancoil2/dispatch]
   ::service [:fancoil2/service]
   ::router [:fancoil2/front-router]
   ::routes [:fancoil2/front-routes]})


(def config
  {;; user field
   ::pconn {:schema (:schema user-plugins)
            :initial-tx []}
   ::ratom {:initial-value {}}
   ::cache {:initial-value {}}
   ::model {}
   ::fx {:dispatch (ig/ref ::dispatch)
         :pconn (ig/ref ::pconn)
         :ratom (ig/ref ::ratom)}
   ::handle {:subscribe (ig/ref ::subscribe)
             :model (ig/ref ::model)
             :cache (ig/ref ::cache)
             :methods (:handle user-plugins)}
   ::process {:handle (ig/ref ::handle)
              :fx (ig/ref ::fx)
              :pconn (ig/ref ::pconn)
              :methods (:process user-plugins)}
   ::subscribe {:ratom (ig/ref ::ratom)
                :pconn (ig/ref ::pconn)
                :methods (:subscribe user-plugins)}
   ::view {:model (ig/ref ::model)
           :dispatch (ig/ref ::dispatch)
           :subscribe (ig/ref ::subscribe)
           :cache (ig/ref ::cache)
           :methods (:view user-plugins)}
   ::chan {}
   ::dispatch {:out-chan (ig/ref ::chan)}
   ::service {:process (ig/ref ::process)
              :in-chan (ig/ref ::chan)}

   ::routes {:routes (:routes user-plugins)}

   ::router {:routes (ig/ref ::routes)
             :dispatch (ig/ref ::dispatch)
             :view (ig/ref ::view)}})


(fc/load-hierarchy hierarchy)

(def app-core
  (ig/init config))

(def app
  (fc/create-system app-core))

(defn update! []
  (rdom/render
   [(app-core ::router) :root-page]
   (.getElementById js/document "app")))

(defn init! []
  (update!))

