(ns film2.system.studio
  (:require
   [integrant.core :as ig]
   [methodology.lib.circuit]

   [film2.modules.editor.m :as editor.m]
   [film2.modules.frame.m :as frame.m]

   [film2.modules.editor.v :as editor.v]

   [film2.modules.editor.s :as editor.s]

   [astronomy.system.solar :as system.solar]))


;; conn

(def schema
  (merge editor.m/schema
         frame.m/schema))

(def initial-tx
  [#:frame{:name "/temp/frame/solar-1.fra"
           :db-string ""
           :scene-type :solar}
   #:editor{:name "default"
            :status :init
            :current-frame [:frame/name "/temp/frame/solar-1.fra"]}])


;; ig


(derive :free-room/scene-lib :circuit/value)
(derive :free-room/conn :circuit/conn)
(derive :free-room/instance-atom :circuit/atom)
(derive :free-room/view :circuit/view)
(derive :free-room/service-chan :circuit/chan)
(derive :free-room/service :circuit/service)



(def default-config
  #:free-room
   {:scene-lib {:solar system.solar/create-system!}
    :conn #:conn {:schema schema
                  :initial-tx initial-tx}
    :instance-atom #:atom{:init-value {}}
    :service-chan #:chan{}
    :service #:service{:service-fn editor.s/init-service!
                       :props {:editor {:db/id [:editor/name "default"]}}
                       :env {:conn (ig/ref :free-room/conn)
                             :instance-atom (ig/ref :free-room/instance-atom)
                             :service-chan (ig/ref :free-room/service-chan)
                             :scene-lib (ig/ref :free-room/scene-lib)}
                       :initial-events [#:event{:action :editor/pull-current-frame}
                                        #:event{:action :editor/load-current-frame}]}
    :view #:view{:view-fn editor.v/EditorView
                 :props {:editor {:db/id [:editor/name "default"]}}
                 :env {:conn (ig/ref :free-room/conn)
                       :service-chan (ig/ref :free-room/service-chan)
                       :instance-atom (ig/ref :free-room/instance-atom)
                       :scene-lib (ig/ref :free-room/scene-lib)}}})


(defn create-app! [config]
  (let [merged-config (merge-with into default-config config)
        system (ig/init merged-config)]
    system))


(comment
  
  (def system (ig/init default-config))

  (:free-room/conn system)


  ;; 
  )