(ns capital21.web
  "Web server for displaying data extracted from World Bank data API as a graph in a browser

   Some references used:
   
   * http://www.vijaykiran.com/2012/02/12/web-application-development-with-clojure-part-4/
   * [Ring wiki](https://github.com/ring-clojure/ring/wiki)
   * [General Lisp editing stuff](http://ergoemacs.org/emacs/emacs_editing_lisp.html)
   * [Bootstrap references](http://twitter.github.io/bootstrap/base-css.html)
   * [clj-time Library](https://github.com/seancorfield/clj-time)"
  (:use     ring.util.response)
  (:use     ring.middleware.params)
  (:use     ring.middleware.resource)
  (:use     ring.middleware.reload)
  (:use     ring.middleware.file)
  (:use     com.duelinmarkers.ring-request-logging)
  (:use     ring.middleware.file-info)
  (:use     ring.middleware.stacktrace)
  (:use incanter.core)
  (:import [java.io ByteArrayOutputStream ByteArrayInputStream])
  (:require [clojure.data.json :as json :refer [ read-str write-str]]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [hiccup.form :as f]
            [hiccup.core :as h]
            [clj-time.core :as time]
            [clj-time.format :as format])
  (:use capital21.core)
  )

(defn stream-plot
  "Generate chart for a given country code as an `InputStream`"
  [country]
  (let [chart (plot-data-set (make-data-set (io/reader "NY.GNP.PCAP.CD_Indicator_en_xml_v2.xml") country))
                     out-stream (ByteArrayOutputStream.)]
                 (save chart out-stream)
                 (ByteArrayInputStream.
                  (.toByteArray out-stream))))

(defn plot [request]
  "Plot handler.

   Respond a PNG image stream for given country"
  (let [{:strs [country]} (:params request)] 
    (response  ( stream-plot country))))

(defn input-form
  [request]
  "Input form handler.

   Outputs an HTML form that allows user to select country code to display GNI per capita"
  (response "foo"))

(defn route
  "Route request according to input URI"
  [request]
  (cond (= "/" (:uri request))     (input-form request)
        (= "/plot" (:uri request)) (plot request)))


(def app
  (->  #'route
       (wrap-reload '[capital21.web capital21.core])
       (wrap-params)                     ;; extract parameters from the query
       (wrap-request-logging)))          ;; log requests

