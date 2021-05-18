(ns methodology.lib.boots)


(defn H1Element [{:title/keys [name]}]
  [:<>
   [:p {:style {:margin "4px 0"
                :font-weight :bold
                :font-size "16px"}}
    name]
   [:hr {:style {:margin "5px 0"}}]])


(defn H2Element [{:title/keys [name]}]
  [:p {:style {:margin "4px 0"
               :font-weight :bold}} name])


(defn ButtonGroupElement
  {:doc "button group"
   :args-sample #:button-group {:buttons [#:button {:id 1
                                                    :name "时"}
                                          #:button {:id 2
                                                    :name "恒星日"}
                                          #:button {:id 3
                                                    :name "日"}]}}
  [{:button-group/keys [buttons] :as props}]
  [:div {:class "btn-group btn-group-sm"
         :role "group"}
   (for [button buttons]
     (let [{:button/keys [id name on-click icon-class]} button]
       ^{:key (:button/id button)}
       [:<>
        [:button {:type "button"
                  :class "btn btn-outline-secondary"
                  :id id
                  :autoComplete "off"
                  :onClick on-click}
         (if icon-class
           [:i {:class icon-class}]
           name)]]))])


(defn RadioGroupElement
  {:doc "button group"
   :args-sample #:button-group {:name "abc"
                                :buttons [#:button {:id 1
                                                    :name "时"}
                                          #:button {:id 2
                                                    :name "恒星日"
                                                    :checked true}
                                          #:button {:id 3
                                                    :name "日"}]}}
  [{:button-group/keys [buttons] :as props}]
  (let [group-name (:button-group/name props)]
    [:div {:class "btn-group btn-group-sm"
           :role "group"}
     (for [button buttons]
       (let [{:button/keys [id name checked on-click]} button]
         ^{:key (:button/id button)}
         [:<>
          [:input {:type "radio"
                   :class "btn-check"
                   :name (str group-name "btnradio")
                   :id (str group-name id) 
                   :defaultChecked checked
                   :autoComplete "off"
                   :onClick on-click}]
          [:label {:class "btn btn-outline-secondary"
                   :for (str group-name id)}
           name]]))])
  )


(defn DropDownElement
  {:doc "dropdown menus "
   :args-sample [#:dropdown {:items
                             [#:dropdown-item{:id 1
                                              :index 0
                                              :name "选择1"
                                              :on-click #(println "action1 clicked")}
                              #:dropdown-item{:id 2
                                              :index 1
                                              :name "选择2"}]
                             :selected-index 0}]}
  [props]
  (let [{:dropdown/keys [items selected-index]} props
        selected (first (filter (fn [i] (= (:dropdown-item/index i) selected-index)) (:dropdown/items props)))]
    [:div {:class "btn-group"
           :style {:margin "2px 0"}}
     [:button {:type "button"
               :class "btn btn-sm btn-secondary"}
      (:dropdown-item/name selected)]
     [:button {:type "button"
               :class "btn btn-sm btn-secondary dropdown-toggle dropdown-toggle-split"
               :data-bs-toggle "dropdown"
               :aria-expanded "false"}
      [:span {:class "visually-hidden"} "Toggle Dropdown"]]
     [:ul {:class "dropdown-menu"
           :style {:font-size "14px"
                   :padding 0}}
      (for [item items]
        (let [{:dropdown-item/keys [index id name on-click]} item]
          ^{:key index}
          [:li [:a {:class "dropdown-item"
                    :on-click on-click}
                name]]))]]))

