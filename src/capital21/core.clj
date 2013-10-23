(ns capital21.core
  "Analyze an input file to extract data.

   Some references used:
   
   * [Tutorial on zippers](http://clojure-doc.org/articles/tutorials/parsing_xml_with_zippers.html)
   * [data.zip API](http://clojure.github.io/data.zip/)
   * [Incanter Core API](http://liebke.github.io/incanter/)"
  (:gen-class)
  (:use ( incanter core stats charts excel))
  (:require [clojure.java.io :as io])
  (:require [pl.danieljanus.tagsoup :as tagsoup])
  (:require [clj-http.client :as client])
  (:require [ clojure.data.xml :as xml1])
  (:require [ clojure.zip :as zip])
  (:require [ clojure.data.zip.xml :as zx])
  (:import [java.io FileInputStream FileOutputStream]
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
;; 
;; We need to take special care for the extracted XML file because it appears to contain a BOM marker 
;; at start which makes XML parser choke. Using BOM should be avoided but it is common practice in Microsoft
;; world to include it when encoding data in UTF-8

(defn copy-without-BOM
  "A version of copy that skips the first 3 bytes of the input stream, assuming they are a BOM character.

   This is a bit gross and brutal as we do not check these 3 skipped bytes are actually the U+FEFF character 
   encoded in UTF-8, and this is a lot of code just to filter copy. A more interesting version would be to
   override the definition of copy for a `FilteredOutputStream`."
  [input output]
  (let [buffer (make-array Byte/TYPE 1024)]
    (loop [offset 0]
      (let [size (.read input buffer)]
        (when (pos? size)
          (do (if (= 0 offset)
                (.write output buffer 3 (- size 3))
                (.write output buffer 0 size))
              (recur (+ offset size))))))
    ))

(defn extract-xml-data-file
  "Extracts XML series file from zipped data."
  [series zip-filename]
  (let [output-name (str series "_Indicator_en_xml_v2.xml")]
    (with-open [zip (ZipFile. zip-filename)
                input (.getInputStream zip (.getEntry zip output-name))
                output (FileOutputStream. output-name)]
      (copy-without-BOM input output))
    output-name))

;; ## Format Data

(defmacro get-data
  [x name]
  `(zx/xml1-> ~x :field (zx/attr= :name ~name) zx/text))

(defn make-data-set
  "Build a data set from given xml data file using given country code
  xml file is expected to comply with the following format:
  
         <?xml version=\"1.0\" encoding=\"utf-8\"?>
           <Root xmlns:wb=\"http://www.worldbank.org\">
            <data>
              <record>
                <field name=\"Country or Area\" key=\"ABW\">Aruba</field>
                <field name=\"Item\" key=\"NY.GNP.PCAP.CD\">GNI per capita, Atlas method (current US$)</field>
                <field name=\"Year\">1960</field>
                <field name=\"Value\" />
              </record>
              <record>
  
  Outputs an incanter dataset built from a each year/value couple found in the XML stream."
  [xml-file country-key]
  (let [elem (zip/xml-zip (xml1/parse xml-file))
        atoi (fn [s] (if (= "" s) 0 (Integer. s)))]
    (dataset  ["year" "value"]
              (map #(first 
                     (for [r  (zx/xml-> (zip/xml-zip %))]
                       [(atoi (get-data r "Year"))
                        (atoi (get-data r "Value"))]))
                   (zx/xml-> 
                    elem
                    :data
                    :record
                    :field
                    (zx/attr= :key country-key)
                    zip/up
                    first)))))

;; ## Plot Data

(defn plot-data-set
  "Plot the given dataset, as produced by make-data-set.

   This uses the `view` function from incanter to plot a graph displayed in a swing window."
  [dataset]
  (with-data dataset
    (xy-plot ($ :year) ($ :value)
             :x-label "Year"
             :y-label "GDP Per capita"
             :title "Evolution of GDP per capita ")))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (println "Hello, World!"))
