(ns film2.system.studio
  (:require
   [integrant.core :as ig]
   [fancoil.core]
   [film2.parts.schema :refer [schema]]
   [film2.parts.root-view]
   [film2.parts.service-center]))


;; ig


(derive :studio/conn :fancoil/db.pconn)
(derive :studio/instance-atom :fancoil/db.atom)
(derive :studio/service-chan :fancoil/async.chan)



(def default-config
  #:studio
   {:conn {:schema schema}
    :instance-atom {:initial-value {}}
    :service-chan {}
    :service-center {:props {:studio {:db/id [:studio/name "default"]}}
                     :env {:conn (ig/ref :studio/conn)
                           :instance-atom (ig/ref :studio/instance-atom)
                           :service-chan (ig/ref :studio/service-chan)}}
    :view {:props {:studio {:db/id [:studio/name "default"]}}
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