(ns astronomy.tools.astronomical-coordinate-tool.m
  (:require
   [astronomy.objects.astronomical-coordinate.m :as astronomical-coordinate]))


(def astronomical-coordinate-tool-1
  #:astronomical-coordinate-tool {:query-type :one-by-name
                                  :query-args-candidates []
                                  :query-args []
                                  :query-result []

                                  :tool/name "astronomical-coordinate-tool"
                                  :tool/chinese-name "天球坐标系工具"
                                  :tool/icon "/image/pirate/earth.jpg"
                                  :tool/type :astronomical-coordinate-tool
                                  :entity/type :astronomical-coordinate-tool})

;; find

(defn cal-query-result [db query-type query-args]
  (case query-type
    :one-by-name (let [one (astronomical-coordinate/pull-one-by-name db (first query-args))]
                   [(:db/id one)])))

;; tx

(defn update-query-type-tx [act-nw query-type]
  [#:astronomical-coordinate-tool {:db/id (:db/id act-nw)
                                   :query-type query-type
                                   :query-args []
                                   :query-result []}])

(defn update-query-args-tx [db act-normal query-args]
  (let [{:astronomical-coordinate-tool/keys [query-type]} act-normal]
    (if (= (first query-args) "未选择")
      [#:astronomical-coordinate-tool {:db/id (:db/id act-normal)
                                       :query-args query-args
                                       :query-result []}]
      [#:astronomical-coordinate-tool {:db/id (:db/id act-normal)
                                       :query-args query-args
                                       :query-result (cal-query-result db query-type query-args)}])))


;; sub

(defn sub-query-args-candidates [conn act-nm]
  (case (:astronomical-coordinate-tool/query-type act-nm)
    :one-by-name (astronomical-coordinate/sub-coordinate-names conn)))
