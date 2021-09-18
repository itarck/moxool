(ns film2.system.studio
  (:require
   [integrant.core :as ig]
   [methodology.lib.circuit]

   [film2.parts.schema :refer [schema]]
   [film2.data.studio :as data.studio]
   [film2.parts.root-view]
   [film2.parts.service-center]

   ))



;; ig


(derive :studio/conn :circuit/conn)
(derive :studio/instance-atom :circuit/atom)
(derive :studio/service-chan :circuit/chan)



(def default-config
  #:studio
   {:conn #:conn {:schema schema
                  :initial-tx data.studio/dataset}
    :instance-atom #:atom{:init-value {}}
    :service-chan #:chan{}
    :service-center #:service{:props {:studio {:db/id [:studio/name "default"]}}
                              :env {:conn (ig/ref :studio/conn)
                                    :instance-atom (ig/ref :studio/instance-atom)
                                    :service-chan (ig/ref :studio/service-chan)}}
    :view #:view{:props {:studio {:db/id [:studio/name "default"]}}
                 :env {:conn (ig/ref :studio/conn)
                       :service-chan (ig/ref :studio/service-chan)
                       :instance-atom (ig/ref :studio/instance-atom)}}})


(defn create-app! [config]
  (let [merged-config (merge-with into default-config config)
        system (ig/init merged-config)]
    system))


(comment
  
  (def system (ig/init default-config))

  (:studio/conn system)


  ;; 
  )