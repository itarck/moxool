(ns film2.data.cinema)


(def default
  #:cinema {:name "default"
            :ioframe-names ["场景1.1：地球和恒星背景"
                            "场景1.2：天球背景和星座"
                            "场景0：基础数据库"]
            :current-ioframe-name "场景1.1：地球和恒星背景"
            :debug? true
            :login-state :initial
            :editor #:editor {:name "default"
                              :doc "编辑ioframe的工具"
                              :current-ioframe [:ioframe/name "场景1.1：地球和恒星背景"]}})
