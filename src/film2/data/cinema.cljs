(ns film2.data.cinema)


(def default
  #:cinema {:name "default"
            :ioframe-names ["场景1.1：地球和恒星背景"
                            "场景1.2：天球背景和星座"
                            "场景1.3：天球坐标系、地球坐标系和地面坐标系"
                            "场景2.1：地球和太阳，观察日和夜"
                            "场景2.2：地球和太阳，黄道，观察季节"
                            "场景2.3：天球坐标系中心的移动"
                            "场景3.1：日心说，太阳中心视角下的五大行星"
                            "场景3.2：地心说，地球中心视角下的五大行星"
                            "场景3.3：地轴的进动"
                            "场景4.1：在地球上观察月相和月球轨道的变化"
                            "场景4.2：三体问题，月球轨道的进动"
                            "探索场景"]
            :current-ioframe-name "场景1.1：地球和恒星背景"
            :debug? true
            :login-state :initial
            :editor #:editor {:name "default"
                              :doc "编辑ioframe的工具"
                              :current-ioframe [:ioframe/name "场景1.1：地球和恒星背景"]}})
