(ns capital21.core
  (:gen-class)
  (:use ( incanter core stats charts excel))
  (:require [clojure.java.io :as io])
  (:require [pl.danieljanus.tagsoup :as tagsoup])
  (:require [clj-http.client :as client])
  (:require [ clojure.data.xml :as xml1])
  (:require [ clojure.zip :as zip])
  (:require [ clojure.data.zip.xml :as zx])
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
  ([series]          (download-xml-in-zip series "out.zip"))
  ([series filename] (io/copy  
                      (:body 
                       (client/get (str "http://api.worldbank.org/v2/en/indicator/" series "?downloadformat=xml") {:as :byte-array})) 
                      (io/file filename))))

;; then we extract the interesting data from the zip file, using standard java API.
(defn extract-xml-data-file
  [series filename]
  (with-open 
      [zip (ZipFile. filename)]
    (let [output-name (str series "_Indicator_en_xml_v2.xml")]
      (io/copy (.getInputStream zip (.getEntry zip output-name))(io/file output-name))
      )))

;; ## Format Data

(defn make-data-set
  ;; build a data set from given xml data file using given country code and key
  ;; xml file is expected to comply with the following format:
  ;;
  ;;        <?xml version="1.0" encoding="utf-8"?>
  ;;          <Root xmlns:wb="http://www.worldbank.org">
  ;;           <data>
  ;;             <record>
  ;;               <field name="Country or Area" key="ABW">Aruba</field>
  ;;               <field name="Item" key="NY.GNP.PCAP.CD">GNI per capita, Atlas method (current US$)</field>
  ;;               <field name="Year">1960</field>
  ;;               <field name="Value" />
  ;;             </record>
  ;;             <record>
  ;; 
  ;; output data set contains two columns, one for year and one for value
  [xml-file country-key item-key]
  (let [elem (zip/xml-zip (xml1/parse xml-file))]
    (map #'xml1/emit-str
         (zx/xml-> 
          elem
          :data
          :record
          :field
          (zx/attr= :key "FRA")
          zip/up
          first))
))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (println "Hello, World!"))
