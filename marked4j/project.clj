(defproject org.clojars.rhg135/marked4j "0.1.0-SNAPSHOT"
  :description "Small library for turning markdown to html using marked"
  :url "https://github.com/rhg/marked4j"
  :license {:name "CC0"
            :url "http://creativecommons.org/publicdomain/zero/1.0/"}
  :main com.rhg135.marked4j.main
  :scm {:name "git"
        :url "https://github.com/rhg/marked4j"
        :dir ".."}
  :dependencies [[org.clojure/clojure "1.8.0" :scope "provided"]
                 [org.clojars.rhg135.marked4j/marked "0.3.2"]])
