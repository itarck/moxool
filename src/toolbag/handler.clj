(ns toolbag.handler
  (:require
   [clojure.java.io :as io]
   [compojure.core :refer [defroutes GET POST routes]]
   [compojure.route :as route]
   [ring-range-middleware.core :as range-middleware]
   [ring.middleware.file :refer [wrap-file]]
   [ring.middleware.format :refer [wrap-restful-format]]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults api-defaults]]))


(def resource-folder "/Users/tizac/room/moxool/resources/public")

;; handlers

(defn echo-handler [request]
  {:status 200
   :body (str request)})

(defn echo-body-params-handler [request]
  {:status 200
   :body (:params request)})


(defn load-db-handler [request]
  (let [params (:body-params request)
        {:keys [db-name]} params
        filename (when db-name (str resource-folder db-name))
        db-value (when (and filename (.exists (io/as-file filename)))
                   (slurp filename))]
    {:status 200
     :body {:status "db loaded"
            :db-name db-name
            :db-value db-value}}))

(defn save-db-handler [request]
  (let [params (:params request)
        {:keys [db-name db-value]} params
        filename (str resource-folder db-name)]
    (when db-value
      (spit filename db-value))
    {:status 200
     :body {:status "db saved"
            :db-name db-name}}))

(defn upload-mp3-handler [request]
  (let [filename (get-in request [:params :filename])
        tempfile (get-in request [:params :file :tempfile])
        localfilename (str resource-folder "/mp3/" filename)]
    (io/copy tempfile (io/file localfilename)))
  {:status 200
   :body {:status "ok"}})

;; routes

(def site-routes
  (routes
   (GET "/hello" [] "Hello World!!!")))

(def api-routes
  (routes
   (GET "/api/dummy-get" [] "get responsed")
   (POST "/api/dummy-post" _request "post responsed")
   (POST "/api/echo-post" request (echo-handler request))
   (POST "/api/body-params" request (echo-body-params-handler request))
   (POST "/api/db/save" request (save-db-handler request))
   (POST "/api/db/load" request (load-db-handler request))
   (POST "/api/mp3/upload" request (upload-mp3-handler request))))

(defroutes default-routes
  (->
   (route/not-found "Not Found")
   (wrap-file "resources/public")
   (range-middleware/wrap-range-header)))

(def app
  (routes
   (-> api-routes
       (wrap-restful-format)
       (wrap-defaults (-> api-defaults
                          (assoc-in [:params :multipart] true))))
   (-> site-routes
       (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] true)))
   default-routes))

