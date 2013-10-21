;; -*- coding: utf-8 -*-
(defproject capital21 "0.1.0-SNAPSHOT"
  :description "Stats for 'Le Capital au XXI siÃ¨cle"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-http "0.6.3"]
                 [clj-tagsoup "0.3.0"]
                 [incanter "1.5.4"]]
  :plugins [[lein-marginalia "0.7.1"]]
  :main capital21.core)
