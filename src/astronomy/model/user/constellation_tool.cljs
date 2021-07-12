(ns astronomy.model.user.constellation-tool
  (:require
   [astronomy.model.constellation :as m.constel]))


(defn get-query-types []
  [{:value :all
    :name "所有星座"}
   {:value :one-by-name
    :name "按星座名字"}
   {:value :by-group
    :name "按星座家族"}])


(defn sub-candinates-by-query-type [conn query-type]
  (case query-type
    :one-by-name (concat ["未选择"] (m.constel/sub-all-constellations-names conn))
    :by-group (concat ["未选择"] (m.constel/sub-all-group-names conn))
    nil))

(defn sub-selected-contellation-ids [conn constellation-tool]
  (let [{:constellation-tool/keys [query-type query-args]} constellation-tool]
    (case query-type
      :all (m.constel/sub-all-constellation-ids conn)
      :one-by-name (if (and (seq query-args) (not= (first query-args) "未选择"))
                     [[:constellation/chinese-name (first query-args)]]
                     [])
      :by-group (if (and (seq query-args) (not= (first query-args) "未选择"))
                  (m.constel/sub-ids-by-group-name conn (first query-args))
                  [])
      [])))


(comment

  (def sample
    #:constellation-tool{:query-type :all
                         :query-args []

                         :tool/name "constellation-tool"
                         :tool/chinese-name "星座"
                         :tool/icon "/image/pirate/earth.jpg"

                         :entity/type :constellation-tool})

;;   
  )


