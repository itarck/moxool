(ns methodology.lib.circuit
  (:require
   [applied-science.js-interop :as j]
   [integrant.core :as ig]
   [cljs.core.async :as async :refer [go >! <! chan]]
   [datascript.core :as d]
   [datascript.transit :as dt]
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [posh.reagent :as p]
   [cljs-http.client :as http]
   [methodology.lib.chest :refer [create-conn!]]))


;; 值模块，直接返回config
(defmethod ig/init-key :circuit/value [_ config]
  config)


;; atom 模块：用的是reagent atom，view也可以自动响应
;; config: {:init-value {:a 1}}
;; instance:  ;; => #object[reagent.ratom.RAtom {:val {:a 1}}]

(defmethod ig/init-key :circuit/atom [_k config]
  (let [{:atom/keys [init-value]} config]
    (atom init-value)))


(defmethod ig/init-key :circuit/ratom [_k config]
  (let [{:ratom/keys [init-value]} config]
    (r/atom init-value)))

;; chan 模块: 不可配置，就是常用的chan
;; config: {}
;; instance：  #object[cljs.core.async.impl.channels.ManyToManyChannel]

(defmethod ig/init-key :circuit/chan [_k _config]
  (chan))


;; conn 模块：datascript数据库，并加了 reagent posh
;; config  
;; #_{:schema schema
;;    :initial-tx initial-tx
;;    :initial-db db}
;; instance 

(defmethod ig/init-key :circuit/conn [_k config]
  (let [{:conn/keys [schema initial-tx db-url initial-db]} config
        conn (create-conn! schema)]
    (when initial-tx
      (d/transact! conn initial-tx))
    (when initial-db
      (d/reset-conn! conn initial-db))
    (when db-url
      (go (let [response (<! (http/get db-url))
                stored-data (:body response)
                stored-db (when stored-data (dt/read-transit-str stored-data))]
            ;; (println stored-db)
            (d/reset-conn! conn stored-db))))
    (p/posh! conn)
    conn))


;; service 模块
;; {:service-fn init-service!
;;  :props {:init-local-state {:status :read-and-write}}
;;  :env {:service-chan (ig/ref :scene/chan)
;;        :scene-conn (ig/ref :scene/conn)}}

(defmethod ig/init-key :circuit/service [_key config]
  (let [{:service/keys [props env service-fn initial-events]} config
        service-chan (get-in env [:service-chan])]

    (service-fn props env)
    (when initial-events
      (doseq [event initial-events]
        (go (>! service-chan event))))
    {:service-chan service-chan}))


;; view 模块

(defmethod ig/init-key :circuit/view [_k config]
  (let [{:view/keys [props view-fn env]} config]
    [view-fn props env]))


;; publisher 模块
;; #_#:publisher {:dispatch-fn (fn [event] (:event/service event))
;;                :sub-chan-names [:solar]
;;                :default-chan-name :solar}

(defmethod ig/init-key :circuit/publisher [_key config]
  (let [{:keys [dispatch-fn sub-chan-names default-chan-name]} config
        pub-chan (chan)
        publication (async/pub pub-chan (fn [event]
                                          (if default-chan-name
                                            (or (dispatch-fn event) default-chan-name)
                                            (dispatch-fn event))))
        sub-chans (into {}
                        (for [chan-name sub-chan-names]
                          [chan-name (chan)]))]
    (doseq [[chan-name channel] sub-chans]
      (async/sub publication chan-name channel))
    {:pub-chan pub-chan
     :publication publication
     :sub-chans sub-chans}))


;; system 模块：把 模块的config 展开成 system config，并实例化系统，返回对外公开的系统接口
;; #_{:system-fn (fn [])
;;    :props {:name :solar}
;;    :publisher {}}

(defmethod ig/init-key :circuit/system [_key config]
  (let [{:system/keys [system-fn props env]} config]
    (system-fn props env)))


;; render模块： 把view 挂载到 app-id
(defmethod ig/init-key :circuit/render [_key config]
  (let [{:render/keys [app-id view]} config]
    (rdom/render view
                 (j/call js/document :getElementById app-id))))

(defmethod ig/init-key :circuit/system-render [_key config]
  (let [{:system-render/keys [app-id system]} config]
    (rdom/render (:system/view system)
                 (j/call js/document :getElementById app-id))))