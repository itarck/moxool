(ns university.app
  (:require
   [applied-science.js-interop :as j]
   [cljs.pprint :refer [pprint]]
   [cljs.reader :refer [read-string]]
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [integrant.core :as ig]

   [fancoil.base :as base]
   [fancoil.unit :as fu]

   [astronomy2.system :as sys]
   [astronomy2.db :as db]))


(def initial-db
  (db/create-db :test-db))

(def user-config
  {::sys/pconn {:initial-db initial-db}})

(defonce domain-system
  (sys/init user-config))

(def entry
  {:db/id [:framework/name "default"]})


;; -------------------------
;; Initialize app

(def platform-atom
  (let [handler-keys (keys (methods base/handle))]
    (r/atom {:handler-keys handler-keys
             :selected-handler (str (first handler-keys))
             :input-value "#:request{:method :todo/set-status :body {:id 3 :status :done}}"
             :output-value ""})))


(defn handler-view
  [domain-system platform-atom]
  (let [handle (::sys/handle domain-system)
        process (::sys/process domain-system)
        tools (:handler-keys @platform-atom)
        selected-tool (:selected-handler @platform-atom)
        input-value (:input-value @platform-atom)
        output-value (:output-value @platform-atom)]
    
    [:div.text-sm
     [:p "Tools"]
     [:select.w-full.my-2.p-2
      {:value (str selected-tool)
       :on-change (fn [e]
                    (let [value (j/get-in e [:target :value])]
                      (swap! platform-atom assoc :selected-handler value)))}
      (for [tool-name tools]
        ^{:key tool-name}
        [:option {:value (str tool-name)} (str tool-name)])]

     [:textarea.w-full.h-60
      {:value input-value
       :on-change (fn [e]
                    (let [value (j/get-in e [:target :value])]
                      (swap! platform-atom assoc :input-value value)))}]
     
     [:div 
      [:button.bg-gray-200.hover:bg-gray-300.p-1.m-1.rounded.border-1.text-sm.float-right
       {:type :button
        :on-click (fn [e]
                    (let [handler-key (read-string selected-tool)
                          input-value (read-string input-value)]
                      (process handler-key input-value)))}
       "commit"]]
     

     [:p.w-full.h-60.bg-white.border.clear-both
      {:value output-value}]]))


(defn platform-view
  [domain-system platform-atom]
  (let [view (::sys/view domain-system)]
    [:div.row.gx-0
     [:div.col-9
      [view :framework/view entry]]
     [:div.col-3.bg-gray-100.p-2
      [handler-view domain-system platform-atom]]]))


(defn update! []
  (rdom/render [platform-view domain-system platform-atom]
               (.getElementById js/document "app")))


(defn ^:export init! []
  (update!))
