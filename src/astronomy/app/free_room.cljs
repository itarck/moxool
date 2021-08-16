(ns astronomy.app.free-room
  (:require
   [cljs.core.async :refer [go go-loop >! <!]]
   [integrant.core :as ig]
   [datascript.transit :as dt]
   [reagent.core :as r]
   [cljs-http.client :as http]
   [methodology.lib.circuit]
   [astronomy.system.solar :as system.solar]))


;; view


(defn WrappedFreeView2 [props {:keys [room-ratom instance-atom]}]
  (let [status (:status @room-ratom)]
    [:div {:style {:position :absolute
                   :height "720px"
                   :width "1280px"}}
     (case status
       :init [:p "init"]
       :ready (get-in @instance-atom [:scene-system :system/view]))]))


;; service


(defmulti handle-event! (fn [_ _ event] (:event/action event)))

(defmethod handle-event! :kick-start
  [props {:keys [room-ratom instance-atom]} event]
  (let [db-url (get-in @room-ratom [:scene :db-url])]
    (go (let [response (<! (http/get db-url))
              stored-data (:body response)
              stored-db (when stored-data (dt/read-transit-str stored-data))
              scene-system (system.solar/create-system! {:initial-db stored-db})]
          (swap! instance-atom assoc :scene-system scene-system)
          (swap! room-ratom assoc-in [:status] :ready)))))


(defn init-service! [props {:keys [service-chan] :as env}]
  (go-loop []
    (let [event (<! service-chan)]
      (handle-event! props env event))
    (recur)))


;; ig


(derive :free-room/room-ratom :circuit/ratom)
(derive :free-room/instance-atom :circuit/atom)
(derive :free-room/view :circuit/view)
(derive :free-room/service-chan :circuit/chan)
(derive :free-room/service :circuit/service)



(def default-config
  #:free-room
   {:room-ratom #:ratom{:init-value {:scene {:db-url "/temp/free-mode.edn"
                                             :scene-type :solar}
                                     :status :init}}
    :instance-atom #:atom{:init-value {}}
    :service-chan #:chan{}
    :service #:service{:service-fn init-service!
                       :props {}
                       :env {:room-ratom (ig/ref :free-room/room-ratom)
                             :instance-atom (ig/ref :free-room/instance-atom)
                             :service-chan (ig/ref :free-room/service-chan)}
                       :initial-events [#:event{:action :kick-start}]}
    :view #:view{:view-fn WrappedFreeView2
                 :props {}
                 :env {:room-ratom (ig/ref :free-room/room-ratom)
                       :instance-atom (ig/ref :free-room/instance-atom)}}})


(defn create-app! [config]
  (let [merged-config (merge-with into default-config config)
        system (ig/init merged-config)]
    system))


(comment

  

  )