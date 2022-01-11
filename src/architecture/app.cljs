(ns architecture.app
  (:require
   [datascript.core :as d]
   [reagent.dom :as rdom]
   [fancoil.base :as base]
   [architecture.system :as sys]))


(def schema
  (::sys/schema (sys/init {})))


(defn create-db []
  (let [conn (d/create-conn schema)
        tx  [(base/model {} :framework/create {})
             (base/model {} :scene/create {:scene/background "black"})
             (base/model {} :user/create {})
             (base/model {} :backpack/create {:backpack/cell [{:backpack-cell/index 0
                                                               :backpack-cell/tool -1}
                                                              {:backpack-cell/index 1
                                                               :backpack-cell/tool -2}]})
             (base/model {} :object/create #:object{:scale [1 1 5]})
             (base/model {} :object/create #:object{:scale [1 1 1]
                                                    :position [-3 0 0]
                                                    :type :cylinder})
             (base/model {} :object/create #:object{:position [3 0 0]
                                                    :scale [3 3 3]
                                                    :type :box})
             (base/model {} :tool/create #:tool{:db/id -1
                                                :type :universe-tool
                                                :name "universe tool"
                                                :chinese-name "宇宙"
                                                :icon "image/moxool/universe.webp"})
             (base/model {} :tool/create #:tool{:db/id -2
                                                :type :clock-tool
                                                :name "clock tool"
                                                :chinese-name "时钟"
                                                :icon "image/moxool/clock.jpg"})]]
    (d/transact! conn tx)
    @conn))

(def initial-db
  (create-db))

(def user-config
  {::sys/pconn {:initial-db initial-db}})

(defonce instance
  (sys/init user-config))

(def entry
  {:db/id [:framework/name "default"]})

;; -------------------------
;; Initialize app

(defn mount-root
  []
  (let [view (::sys/view instance)]
    (rdom/render [view :framework/view entry]
                 (js/document.getElementById "app"))))


(defn ^:export init! []
  (mount-root))
