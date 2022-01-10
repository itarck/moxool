(ns laboratory.app.playground
  (:require
   [fancoil.base :as base]
   [reagent.dom :as rdom]
   [laboratory.system.zero :as zero]
   [cljs.spec.alpha :as s]
   [fancoil.unit :as fu]))


(def initial-tx
  [(base/model {} :framework/create {})
   (base/model {} :scene/create {:scene/background "black"})
   (base/model {} :user/create {})
   (base/model {} :backpack/create {:backpack/cell [{:backpack-cell/index 0
                                                     :backpack-cell/tool -1}
                                                    {:backpack-cell/index 1
                                                     :backpack-cell/tool -2}]})
   (base/model {} :object/create #:object{:scale [1 1 5]})
   (base/model {} :object/create #:object{:position [3 0 0]
                                          :scale [3 3 3]})
   (base/model {} :tool/create #:tool{:db/id -1
                                      :name "universe tool"
                                      :chinese-name "宇宙"
                                      :icon "image/moxool/universe.webp"})
   (base/model {} :tool/create #:tool{:db/id -2
                                      :name "clock tool"
                                      :chinese-name "时钟"
                                      :icon "image/moxool/clock.jpg"})])



(def user-config 
  {::zero/pconn {:initial-tx initial-tx}})

(defonce instance
  (zero/init user-config))

(def entry
  {:db/id [:framework/name "default"]})

;; -------------------------
;; Initialize app

(defn mount-root
  []
  (let [view (::zero/view instance)]
    (rdom/render [view :framework/view entry]
                 (js/document.getElementById "app"))))


(defn ^:export init! []
  (mount-root))


(comment
  (def model
    (partial base/model {}))

  (model :framework/create
         #:framework {:db/id -1
                      :scene -2
                      :user -3}))


(comment

  (s/valid? :db/id 324)

  (s/check-asserts true)

  (let [spec (::fu/spec instance)]
    (spec :assert :entity/entity {:db/id [:scene/name "34"]}))

  )
