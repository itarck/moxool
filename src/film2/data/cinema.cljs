(ns film2.data.cinema)


(def default
  #:cinema {:name "default"
            :ioframe-names ["1.太阳和地球的小型系统"
                            "2.太阳、地球和五大行星"]
            :current-ioframe-name "1.太阳和地球的小型系统"
            :editor #:editor {:name "default"
                              :doc "编辑ioframe的工具"
                              :current-ioframe [:ioframe/name "1.太阳和地球的小型系统"]}})
