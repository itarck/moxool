(ns film2.data.cinema)


(def default
  #:cinema {:name "default"
            :ioframe-names ["a" "b" "c"]
            :editor #:editor {:name "default"
                              :doc "编辑ioframe的工具"
                              :current-ioframe [:ioframe/name "mini-1"]}})
