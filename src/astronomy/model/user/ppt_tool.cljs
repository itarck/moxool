(ns astronomy.model.user.ppt-tool
  (:require
   [posh.reagent :as p]))

(def schema {:ppt/chinese-name {:db/unique :db.unique/identity}
             :ppt-tool/ppts {:db/valueType :db.type/ref
                             :db/cardinality :db.cardinality/many}})


(def sample
  #:ppt {:pages [#:ppt-page{:image-url "/image/lunar/page1.png"}
                 #:ppt-page{:image-url "/image/lunar/page2.png"}]
         :chinese-name "1.天文尺度"
         :current-page 0})


(def sample2
  #:ppt-tool {:query-type :ppt-by-name
              :query-args ["1.天文尺度"]
              :ppts [sample]
              :tool/name "ppt tool"
              :tool/chinese-name "脚本"
              :tool/icon "/image/moxool/ppt.jpg"
              :entity/type :ppt-tool})

;; subs

(defn sub-all-ppt-names [conn]
  (sort @(p/q '[:find [?chinese-name ...]
                :where [?id :ppt/chinese-name ?chinese-name]]
              conn)))


;; models

(defn current-page [ppt1]
  (get (:ppt/pages ppt1) (:ppt/current-page ppt1)))


(defn count-pages [ppt1]
  (count (:ppt/pages ppt1)))


(defn next-page-tx [ppt1]
  (let [current-page-index (:ppt/current-page ppt1)]
    (when (< current-page-index (dec (count-pages ppt1)))
      [{:db/id (:db/id ppt1)
        :ppt/current-page (inc current-page-index)}])))

(defn prev-page-tx [ppt1]
  (let [current-page-index (:ppt/current-page ppt1)]
    (when (> current-page-index 0)
      [{:db/id (:db/id ppt1)
        :ppt/current-page (dec current-page-index)}])))

