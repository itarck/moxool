(ns astronomy.model.user.horizon-coordinate-tool
  (:require
   [astronomy.model.horizon-coordinate :as m.horizon-coordinate]))


(def horizon-coordinate-tool
  #:horizon-coordinate-tool {:query-type :one-by-name
                             :query-args-candidates []
                             :query-args []
                             :query-result []

                             :tool/name "horizon-coordinate-tool"
                             :tool/chinese-name "地平坐标系工具"
                             :tool/icon "/image/pirate/earth.jpg"
                             :tool/type :horizon-coordinate-tool
                             :entity/type :horizon-coordinate-tool})

;; find
(defn get-query-args-candidates [db query-type]
  (case query-type
    :one-by-name (m.horizon-coordinate/find-horizon-coordinate-names db)))

(defn cal-query-result [db query-type query-args]
  (case query-type
    :one-by-name (let [one (m.horizon-coordinate/pull-one-by-name db (first query-args))]
                   [(:db/id one)])))

;; tx

(defn update-query-type-tx [hct-nw query-type]
  [#:horizon-coordinate-tool {:db/id (:db/id hct-nw)
                              :query-type query-type
                              :query-args []
                              :query-result []}])

(defn update-query-args-tx [db hct-normal query-args]
  (let [{:horizon-coordinate-tool/keys [query-type]} hct-normal]
    (if (= (first query-args) "未选择")
      [#:horizon-coordinate-tool {:db/id (:db/id hct-normal)
                                  :query-args query-args
                                  :query-result []}]
      [#:horizon-coordinate-tool {:db/id (:db/id hct-normal)
                                  :query-args query-args
                                  :query-result (cal-query-result db query-type query-args)}])))


;; sub

(defn sub-query-args-candidates [conn hct-nm]
  (case (:horizon-coordinate-tool/query-type hct-nm)
    :one-by-name (m.horizon-coordinate/sub-horizon-coordinate-names conn)))

