(ns film.app.slider.free-mode
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :as async :refer [go >! <! chan alts! timeout put! take! go-loop]]
   [reagent.core :as r]
   [reagent.dom :as dom]
   [integrant.core :as ig]
   [posh.reagent :as p]
   [methodology.lib.circuit :as circuit]))


;; scene bmi

(def schema {:slider/name {:db/unique :db.unique/identity}})

(defn sub-sliders [scene-conn]
  (let [ids @(p/q '[:find [?id ...]
                    :where [?id :slider/name _]]
                  scene-conn)
        sliders (doall (map (fn [id] @(p/pull scene-conn '[*] id)) ids))]
    sliders))


;; service

(defn init-view-service! [props env]
  (let [{:keys [service-chan scene-conn meta-atom]} env]
    (go-loop []
      (let [event (<! service-chan)]
        (when (= (:status @meta-atom) :read-and-write)
          (case (:event/action event)
            :slider/on-change (let [{:keys [slider new-value]} (:event/detail event)]
                                (p/transact! scene-conn [[:db/add (:db/id slider) :slider/value new-value]]))
            (println event))))
      (recur))))


;; view

(defn Slider [value min max on-change]
  [:input {:type "range" :value value :min min :max max
           :style {:width "100%"}
           :on-change on-change}])

(defn RootPage [props env]
  (let [{:keys [service-chan scene-conn]} env
        sliders (sub-sliders scene-conn)]
    [:div {:style {:height "720px"
                   :width "1280px"}}
     (for [slider sliders]
       ^{:key (:db/id slider)}
       [:div
        [:p (:db/id slider)]
        [Slider (:slider/value slider) 0 100
         (fn [e] (let [new-value (.. e -target -value)]
                   (go (>! service-chan
                           #:event {:action :slider/on-change
                                    :detail {:slider slider
                                             :new-value new-value}}))))]])]))


;; ig

(derive ::conn :circuit/conn)
(derive ::chan :circuit/chan)
(derive ::service :circuit/service)
(derive ::view :circuit/view)


(def default-initial-tx [{:slider/name "bmi"
                          :slider/value 30}])

(defn create-scene-system [props env]
  (let [{:keys [initial-tx]} props
        {:keys [meta-atom]} env
        config {::conn #:conn {:schema schema
                               :initial-tx (or initial-tx default-initial-tx)}

                ::chan #:chan {}
                ::service #:service {:service-fn init-view-service!
                                     :props {}
                                     :env {:service-chan (ig/ref ::chan)
                                           :scene-conn (ig/ref ::conn)
                                           :meta-atom meta-atom}}

                ::view #:view {:view-fn RootPage
                               :props {}
                               :env {:service-chan (ig/ref ::chan)
                                     :scene-conn (ig/ref ::conn)}}}
        instance (ig/init config)]
    #:system {:view (::view instance)
              :conn (::conn instance)
              :meta-atom meta-atom
              :api-chan (::api-chan instance)}))


;; mount point

(def meta-atom (r/atom {:status :read-and-write}))

(defonce system
  (create-scene-system {} {:meta-atom meta-atom}))

(defn update! []
  (dom/render
   (:system/view system)
   (j/call js/document :getElementById  "app")))

(defn ^:export init! []
  (update!))
