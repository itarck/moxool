(ns film2.data.cinema)


(def default
  #:cinema {:name "default"
            :ioframe-names ["mini-1" "mini-2" "city-1"]
            :current-ioframe-name "mini-1"
            :editor #:editor {:name "default"
                              :doc "编辑ioframe的工具"
                              :current-ioframe [:ioframe/name "mini-1"]}})
