(ns astronomy.model.user.terrestrial-coordinate-tool
  (:require
   [astronomy.objects.terrestrial-coordinate.m :as terrestrial-coordinate.m]))


(def terrestrial-coordinate-tool-1
  #:terrestrial-coordinate-tool {:query-type :one-by-name
                                 :query-args-candidates []
                                 :query-args []
                                 :query-result []

                                 :tool/name "terrestrial-coordinate-tool"
                                 :tool/chinese-name "地球坐标系工具"
                                 :tool/icon "/image/pirate/earth.jpg"
                                 :tool/type :terrestrial-coordinate-tool
                                 :entity/type :terrestrial-coordinate-tool})

;; find
(defn get-query-args-candidates [db query-type]
  (case query-type
    :one-by-name (terrestrial-coordinate.m/find-coordinate-names db)))

(defn cal-query-result [db query-type query-args]
  (case query-type
    :one-by-name (let [one (terrestrial-coordinate.m/pull-one-by-name db (first query-args))]
                   [(:db/id one)])))

;; tx

(defn update-query-type-tx [act-nw query-type]
  [#:terrestrial-coordinate-tool {:db/id (:db/id act-nw)
                                  :query-type query-type
                                  :query-args []
                                  :query-result []}])

(defn update-query-args-tx [db act-normal query-args]
  (let [{:terrestrial-coordinate-tool/keys [query-type]} act-normal]
    (if (= (first query-args) "未选择")
      [#:terrestrial-coordinate-tool {:db/id (:db/id act-normal)
                                      :query-args query-args
                                      :query-result []}]
      [#:terrestrial-coordinate-tool {:db/id (:db/id act-normal)
                                      :query-args query-args
                                      :query-result (cal-query-result db query-type query-args)}])))


;; sub

(defn sub-query-args-candidates [conn act-nm]
  (case (:terrestrial-coordinate-tool/query-type act-nm)
    :one-by-name (terrestrial-coordinate.m/sub-coordinate-names conn)))
