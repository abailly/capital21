(ns capital21.core
  (:gen-class)
  (:use ( incanter core stats charts excel))
  (:require [clojure.java.io :as io])
  (:require [pl.danieljanus.tagsoup :as tagsoup])
  (:require [clj-http.client :as client])
  (:import [java.io FileInputStream]
           [java.util.zip ZipFile]))

;; # Scraping the World Bank Database
;; 
;; * [WorldBank Database](http://data.worldbank.org/indicator/NY.GNP.PCAP.CD)
;; * [Getting started with emacs+clojure](http://clojure-doc.org/articles/tutorials/emacs.html#toc_6)
;;
;; ## Retrieve data
;;
;; World bank happens to have an API at http://api.worldbank.org/v2/. From this API one can retrieve data series in various formats: CSV, Excel, XML
;; Let's first retrieve the data as XML which is easier to work with in clojure. Data is packed in a zip file so first we need to download
;; the zip to some file

(defn download-xml-in-zip
  "Download given series and put it in a file."
  ([series]          (get-xml-data series "out.zip"))
  ([series filename] (io/copy  
                      (:body 
                       (client/get (str "http://api.worldbank.org/v2/en/indicator/" series "?downloadformat=xml") {:as :byte-array})) 
                      (io/file filename))))

;; then we extract the interesting data from the zip file, using standard java API.
(defn extract-xml-data-file
  "extract XML data for given series from given filename"
  [series filename]
  (with-open 
      [zip (ZipFile. filename)]
    (let [output-name (str series "_Indicator_en_xml_v2.xml")]
      (io/copy (.getInputStream zip (.getEntry zip output-name))(io/file output-name))
      )
    ))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (println "Hello, World!"))
