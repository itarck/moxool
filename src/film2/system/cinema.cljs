(ns film2.system.cinema
  (:require
   [integrant.core :as ig]
   [fancoil.core]
   [film2.parts.schema :refer [schema]]
   [film2.parts.root-view]
   [film2.parts.service-center]))


;; ig


(derive :cinema/conn :fancoil/db.pconn)
(derive :cinema/instance-atom :fancoil/db.atom)
(derive :cinema/service-chan :fancoil/async.chan)



(def default-config
  #:cinema
   {:conn {:schema schema}
    :instance-atom {:initial-value {}}
    :service-chan {}
    :service-center {:props {:cinema {:db/id [:cinema/name "default"]}}
                     :env {:conn (ig/ref :cinema/conn)
                           :instance-atom (ig/ref :cinema/instance-atom)
                           :service-chan (ig/ref :cinema/service-chan)}}
    :view {:props {:cinema {:db/id [:cinema/name "default"]}}
           :env {:conn (ig/ref :cinema/conn)
                 :service-chan (ig/ref :cinema/service-chan)
                 :instance-atom (ig/ref :cinema/instance-atom)}}})


(defn create-app! [config]
  (let [merged-config (merge-with into default-config config)
        system (ig/init merged-config)]
    system))


(comment

  (def system (ig/init default-config))

  (:cinema/conn system)


  ;; 
  )