(ns film2.system.studio
  (:require
   [integrant.core :as ig]
   [methodology.lib.circuit]

   [film2.modules.editor.m :as editor.m]
   [film2.modules.ioframe.m :as ioframe.m]

   [film2.modules.editor.v :as editor.v]
   [film2.modules.editor.s :as editor.s]

   [astronomy.system.solar :as system.solar]))


;; conn

(def schema
  (merge editor.m/schema
         ioframe.m/schema))


(def initial-tx
  [#:ioframe {:type :mini
              :name "mini-1"
              :db-url "/temp/frame/solar-1.fra"
              :description "只有太阳和地球的小型系统"}
   #:ioframe {:type :mini
              :name "mini-2"
              :db-url "/temp/frame/solar-2.fra"
              :description "只有太阳和地球的小型系统"}
   #:editor{:name "default"
            :status :init
            :current-frame [:ioframe/name "mini-2"]}])


;; ig


(derive :studio/scene-lib :circuit/value)
(derive :studio/conn :circuit/conn)
(derive :studio/instance-atom :circuit/atom)
(derive :studio/view :circuit/view)
(derive :studio/service-chan :circuit/chan)
(derive :studio/service :circuit/service)



(def default-config
  #:studio
   {:scene-lib {:solar system.solar/create-system!}
    :conn #:conn {:schema schema
                  :initial-tx initial-tx}
    :instance-atom #:atom{:init-value {}}
    :service-chan #:chan{}
    :service #:service{:service-fn editor.s/init-service!
                       :props {:editor {:db/id [:editor/name "default"]}}
                       :env {:conn (ig/ref :studio/conn)
                             :instance-atom (ig/ref :studio/instance-atom)
                             :service-chan (ig/ref :studio/service-chan)
                             :scene-lib (ig/ref :studio/scene-lib)}
                       :initial-events [#:event{:action :editor/load-current-frame}]}
    :view #:view{:view-fn editor.v/EditorView
                 :props {:editor {:db/id [:editor/name "default"]}}
                 :env {:conn (ig/ref :studio/conn)
                       :service-chan (ig/ref :studio/service-chan)
                       :instance-atom (ig/ref :studio/instance-atom)
                       :scene-lib (ig/ref :studio/scene-lib)}}})


(defn create-app! [config]
  (let [merged-config (merge-with into default-config config)
        system (ig/init merged-config)]
    system))


(comment
  
  (def system (ig/init default-config))

  (:studio/conn system)


  ;; 
  )