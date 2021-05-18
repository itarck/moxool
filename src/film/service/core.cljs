(ns film.service.core
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :as async :refer [go >! <! chan alts! timeout put! take! go-loop]]
   [film.service.player :as s.player]
   [film.service.editor :as s.editor]))



(def modules
  [#:module {:name :editor
             :listen [:editor]
             :service-fn s.editor/init-service!}

   #:module {:name :player
             :listen [:player]
             :service-fn s.player/init-service!}])


(defn init-service-center! [props env]
  (let [{:keys [studio-chan studio-conn scene-system]} env
        modules-dispatch-fn (fn [event]
                              (keyword (namespace (second event))))
        modules-publication (async/pub studio-chan modules-dispatch-fn)
        studio-env {:system-conn studio-conn
                    :scene-system scene-system
                    :out-chan studio-chan}]

    (doseq [{:module/keys [service-fn listen]} modules]
      (let [module-chan (chan)]
        (doseq [l listen]
          (async/sub modules-publication l module-chan))
        (service-fn props (assoc studio-env :in-chan module-chan))))

    {:service-chan studio-chan}))

