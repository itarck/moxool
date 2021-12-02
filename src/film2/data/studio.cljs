(ns film2.data.studio)


(def default
  #:studio {:name "default"
            :mode :editor
            :editor #:editor {:name "default"
                              :doc "编辑ioframe的工具"
                              :current-ioframe [:ioframe/name "mini-1"]}
            :player #:player {:name "default"
                              :doc "播放iovideo的工具"
                              :current-iovideo [:iovideo/name "mini move"]}
            :recorder #:recorder {:name "default"
                                  :doc "编辑和录制iovideo的工具"
                                  :current-menu :create-iovideo
                                  :current-iovideo [:iovideo/name "mini move"]}})
