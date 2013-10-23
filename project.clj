;; -*- coding: utf-8 -*-
(defproject capital21 "0.1.0-SNAPSHOT"
  :description "Simple data retrieval demo based on World Bank data series for GDP per capita.

Initially wanted to use data from  'Le Capital au XXI si&egrave;cle' by T.Piketty but website
is unusable and data cannot be retrieved and used without a lot of massage, so I resorted to
use datasets from World Bank API. 

Aims at demonstrating some features of developing clojure with emacs, incanter, ring and couple other libs.

References used:

* [Tutorial on clojure+emacs](http://clojure-doc.org/articles/tutorials/emacs.html)
* [World Bank GDP per capita](http://data.worldbank.org/indicator/NY.GNP.PCAP.CD)
* [World Top income database](http://topincomes.parisschoolofeconomics.eu/)"
  :url "http://github.com/abailly/capital21"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-http "0.6.3"]
                 [org.clojure/data.zip "0.1.1"]
                 [org.clojure/data.xml "0.0.7"]
                 [clj-tagsoup "0.3.0"]
                 [incanter "1.5.4"]
                 ;; web stuff
                 [hiccup "1.0.3"]
                 [ring "1.1.8"]
                 [com.duelinmarkers/ring-request-logging "0.2.0"]]
  :plugins [[lein-marginalia "0.7.1"]
            [lein-ring "0.8.6"]]
  :ring {:handler capital21.web/app})
