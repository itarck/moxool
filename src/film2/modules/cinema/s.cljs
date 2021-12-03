(ns film2.modules.cinema.s
  (:require
   [applied-science.js-interop :as j]
   [astronomy.service.effect :as fx]
   [datascript.core :as d]
   [film2.modules.cinema.m :as cinema.m]))


(defmulti handle-event (fn [_ _ event] (:event/action event)))


(defmethod handle-event :cinema/change-ioframe
  [props env {{:keys [cinema ioframe-name]} :event/detail}]
  (let [effects (fx/effects :tx [#:cinema {:db/id (:db/id cinema)
                                           :current-ioframe-name ioframe-name}]
                            :event #:event {:action :editor/change-current-ioframe
                                            :detail {:editor (:cinema/editor cinema)
                                                     :ioframe {:db/id [:ioframe/name ioframe-name]}}}
                            :event #:event {:action :editor/load-current-ioframe
                                            :detail {:editor (:cinema/editor cinema)}})]
    effects))


(defmethod handle-event :cinema/varify-angel-code
  [props {:keys [db]} {:event/keys [detail]}]
  (let [{:keys [cinema email angel-code from-user?]} detail]
    (when from-user?
      (j/call js/localStorage :setItem "email" email)
      (j/call js/localStorage :setItem "angel-code" angel-code))
    (let [cinema (d/pull db '[*] (:db/id cinema))]
      (if (or (cinema.m/varify-angel-code email angel-code) (:cinema/debug? cinema))
        (fx/effects :tx [{:db/id (:db/id cinema)
                          :cinema/login-state :success}]
                    :event #:event {:action :editor/load-current-ioframe
                                    :detail {:editor {:db/id [:editor/name "default"]}}})
        (fx/effects :tx [{:db/id (:db/id cinema)
                          :cinema/login-state :fail}])))
    ))


(defmethod handle-event :cinema/login-from-localstorage
  [props env {:event/keys [detail]}]
  (let [email (j/call js/localStorage :getItem "email")
        angel-code (j/call js/localStorage :getItem "angel-code")]
    (fx/effects :event #:event {:action :cinema/varify-angel-code
                                :detail {:cinema (:cinema detail)
                                         :email email
                                         :angel-code angel-code}})))


(defmethod handle-event :default
  [props env {:event/keys [detail]}]
  (println "film2.modules.cinema.s: default: " detail))