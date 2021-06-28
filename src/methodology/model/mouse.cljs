(ns methodology.model.mouse)


(def schema {})


(def mouse-1
  #:mouse {:page-x 1
           :page-y -1
           :entity/name "default mouse"})


(defn update-mouse-position-tx [mouse page-x page-y]
  [{:db/id (:db/id mouse)
    :mouse/page-x page-x
    :mouse/page-y page-y}])

(comment
  
  )