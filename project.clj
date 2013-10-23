;; -*- coding: utf-8 -*-
(defproject capital21 "0.1.0-SNAPSHOT"
  :description "Stats for 'Le Capital au XXI siècle"
  :url "http://example.com/FIXME"
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
