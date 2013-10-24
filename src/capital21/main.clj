(ns capital21.main
  "Main application namespace.
   
   Runs jetty server on PORT given by environment variable, or 3000 by default"
  (:gen-class)
  (:use capital21.web)
  (:require  [ring.adapter.jetty :as j]))


(defn -main []
  "Main application, executable with `lein run`

   This main is duplicate with what ring handler provides but is useful when packaging application as standalone."
  (let [port-env (get (System/getenv) "PORT")
        port     (if (nil? port-env) 3000 (Integer/parseInt port-env))]
    (j/run-jetty app {:port port})))

