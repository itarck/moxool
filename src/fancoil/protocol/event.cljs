(ns fancoil.protocol.event)


(def event-sample 
  #:event {:from :local-storage
           :to :local-storage
           :action :local-storage/set
           :detail {:data "abc"}
           :callback (fn [])})


(defn create-event
  [action detail & args]
  (let [args-map (apply hash-map args)
        from (or (:from args-map) :view)
        to (or (:to args-map) (keyword (namespace action)))
        callback (:callback args-map)]
    (-> #:event {:from from
                 :to to
                 :action action
                 :detail detail}
        ((fn [event]
           (if callback
             (assoc event :callback callback)
             event))))))