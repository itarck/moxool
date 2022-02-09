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
  (let [handle (::fu/handle domain-system)
        process (::fu/process domain-system)
        tools (:handler-keys @platform-atom)
        selected-tool (:selected-handler @platform-atom)
        input-value (:input-value @platform-atom)
        output-value (:output-value @platform-atom)]
    [:div "tool view"
     [:select.form-select {:style {:width "90%"}
                           :value (str selected-tool)
                           :on-change (fn [e]
                                        (let [value (j/get-in e [:target :value])]
                                          (swap! platform-atom assoc :selected-handler value)))}
      (for [tool-name tools]
        ^{:key tool-name}
        [:option {:value (str tool-name)} (str tool-name)])]

     [:textarea {:style {:width "90%"
                         :height "260px"}
                 :value input-value
                 :on-change (fn [e]
                              (let [value (j/get-in e [:target :value])]
                                (swap! platform-atom assoc :input-value value)))}]
     
     [:button {:type :button
               :class "btn btn-light"
               :on-click (fn [e]
                           (let [handler-key (read-string selected-tool)
                                 input-value (read-string input-value)
                                 output-value (handle handler-key input-value)]
                             (swap! platform-atom assoc :output-value (str output-value))
                             (process handler-key input-value)))}
      "commit"]

     [:textarea {:style {:width "90%"
                         :height "260px"}
                 :value output-value}]]))


(defn platform-view
  [domain-system platform-atom]
  (let [view (::sys/view domain-system)]
    [:div.row.gx-0
     [:div.col-9
      [view :framework/view entry]]
     [:div.col-3.bg-gray-300.p-2
      [handler-view domain-system platform-atom]]]))


(defn update! []
  (rdom/render [platform-view domain-system platform-atom]
               (.getElementById js/document "app")))


(defn ^:export init! []
  (update!))
