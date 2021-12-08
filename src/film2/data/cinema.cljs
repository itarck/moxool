(ns film2.data.cinema)


(def default
  #:cinema {:name "default"
            :ioframe-names ["场景1.1：地球和恒星背景"
                            "场景1.2：天球背景和星座"
                            "场景1.3：天球坐标系、地球坐标系和地面坐标系"
                            "场景2.1：地球和太阳，观察日和夜"
                            "场景2.2：地球和太阳，黄道，观察季节"
                            "场景2.3：天球坐标系中心的移动"]
            :current-ioframe-name "场景1.1：地球和恒星背景"
            :debug? true
            :login-state :initial
            :editor #:editor {:name "default"
                              :doc "编辑ioframe的工具"
                              :current-ioframe [:ioframe/name "场景1.1：地球和恒星背景"]}})
