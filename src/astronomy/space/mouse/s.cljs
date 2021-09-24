(ns astronomy.space.mouse.s
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :as async :refer [go >! <! go-loop]]
   [datascript.core :as d]
   [astronomy.space.mouse.m :as m.mouse]
   [astronomy.component.mouse :as c.mouse]
   [posh.reagent :as p]))


(defn init-mouse-listener! [props {:keys [service-chan] :as env}]
  (j/call js/document
          :addEventListener "mousemove"
          (fn [e]
            (let [x (j/get e :clientX)
                  y (- (j/get e :clientY) 80)]
              (go (>! service-chan #:event {:action :mouse/move
                                            :detail {:mouse-position [x y]
                                                     :page-x x
                                                     :page-y y}})))))
  (j/call js/document
          :addEventListener "click"
          (fn [e]
            (let [x (j/get e :clientX)
                  y (- (j/get e :clientY) 80)]
              (go (>! service-chan #:event {:action :mouse/on-click
                                            :detail {:mouse-position [x y]
                                                     :alt-key (j/get-in e [:altKey])
                                                     :meta-key (j/get-in e [:metaKey])
                                                     :shift-key (j/get-in e [:shiftKey])}})))))

  (j/call js/document :addEventListener "wheel"
          (fn [e]
            (let [delta (j/get-in e [:wheelDelta])]
              (go (>! service-chan #:event {:action :user/mouse-wheeled
                                            :detail {:delta delta
                                                     :alt-key (j/get-in e [:altKey])
                                                     :meta-key (j/get-in e [:metaKey])
                                                     :shift-key (j/get-in e [:shiftKey])}}))))))


(defmulti handle-event! (fn [props env event] (:event/action event)))

(defmethod handle-event! :mouse/log
  [props env {:event/keys [detail]}]
  (println detail))


(defmethod handle-event! :mouse/move
  [props {:keys [conn meta-atom]} {:event/keys [detail]}]
  (when (and meta-atom (= (:mode @meta-atom) :read-and-write))
    (let [user (d/pull @conn '[{:user/mouse [*]}] (get-in props [:user :db/id]))
          {:keys [page-x page-y]} detail
          tx (m.mouse/update-mouse-position-tx (:user/mouse user) page-x page-y)]
      (p/transact! conn tx))))

(defmethod handle-event! :mouse/on-click
  [props {:keys [dom-atom service-chan]} {:event/keys [detail]}]
  (let [normalized-position (c.mouse/get-normalized-mouse (:three-instance @dom-atom))
        mouse-direction (c.mouse/get-mouse-direction-vector3 (:three-instance @dom-atom))
        event #:event{:action :user/mouse-clicked
                      :detail (-> detail
                                  (assoc :mouse-normalized-position normalized-position)
                                  (assoc :mouse-direction (vec mouse-direction)))}]
    (go (>! service-chan event))))


;; service

(defn init-service! [props {:keys [process-chan] :as env}]
  (println "mouse service started")
  (init-mouse-listener! props env)
  (go-loop []
    (let [event (<! process-chan)]
      (try
        (handle-event! props env event)
        (catch js/Error e
          (js/console.log e))))
    (recur)))

