(ns astronomy.service.keyboard-listener
  (:require
   [applied-science.js-interop :as j]))


(defn init-keyboard-listener! [props {:keys [service-chan] :as env}]
  (j/call js/document
          :addEventListener "keydown"
          (fn [e]
            (let [keydown (j/get e :key)]
              (cond
                (= "w" keydown) (println "keyboard w")

                (= "s" keydown) (println "keyboard s")

                (= "a" keydown) (println "keyboard a")

                (= "d" keydown) (println "keyboard d")

                (= "z" keydown) (println "keyboard z")
                :else nil)))))


(defn init-service! [props env]
  (init-keyboard-listener! props env))



