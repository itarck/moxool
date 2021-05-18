(ns toolbag.core
  (:require
   [toolbag.handler :refer [app]]
   [integrant.core :as ig]
   [ring.util.response :as resp]
   [ring.adapter.jetty :as jetty]
   [org.httpkit.server :as httpkit])
  (:gen-class))


(defmethod ig/init-key :adapter/jetty [_ {:keys [handler] :as opts}]
  (println "init jetty!!!")
  (jetty/run-jetty handler (-> opts (dissoc :handler) (assoc :join? false))))

(defmethod ig/halt-key! :adapter/jetty [_ server]
  (println "halt jetty!!!")
  (.stop server))

(defmethod ig/init-key :adapter/httpkit [_ {:keys [handler] :as opts}]
  (println "init httpkit!!!")
  (httpkit/run-server handler (-> opts (dissoc :handler) (assoc :join? false))))

(defmethod ig/halt-key! :adapter/httpkit [_ server]
  (println "halt httpkit!!!")
  (.stop server))

(defmethod ig/init-key :handler/greet [_ {:keys [name]}]
  (fn [_] (resp/response (str "Hello " name))))

(defmethod ig/init-key :handler/app [_key _config]
  app)


(def config
  {;
  ;;  :adapter/httpkit {:port 7000, :handler (ig/ref :handler/app)}
   :adapter/jetty {:port 7000, :handler (ig/ref :handler/app)}
   :handler/app {}})

(defn -main [& args]
  (ig/init config))


