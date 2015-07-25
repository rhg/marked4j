(ns marked4j.core
  (:refer-clojure :exclude (contains?))
  (:require [clojure.java.io :as io])
  (:import [java.net URL]
           [javax.script ScriptEngineManager ScriptEngine Invocable]
           (clojure.lang IPersistentSet)))

(defn- ^ScriptEngine engine
  "Returns an engine for a given name. js by default"
  ([^String name] (.getEngineByName (ScriptEngineManager.) name))
  ([] (engine "javascript")))

(defn- default-marked
  "Return the bundled marked.min.js 0.3.2"
  []
  (-> (io/resource "marked.min.js")))

(def default-options
  (zipmap #{:gfm :tables :breaks :sanitize :pedantic :smartypants :smartLists}
          (repeat false)))

(defn- load-marked
  "Evals the marked js"
  ([engine ^URL url]
   (.eval engine (slurp url)))
  ([engine] (load-marked engine (default-marked))))

(defn- gfm-opts
  "Returns gfm-specific options"
  [gfm]
  (into {:tables false :breaks false}
        (zipmap (or gfm #{})
                (repeat true))))

(defrecord MarkedOptions [gfm pedantic sanitize smartLists smartypants])

(defn- coerce-bool
  [m k]
  (update-in m [k] boolean))

(defn- coerce-opts
  [m]
  (let [{:keys [gfm]} m]
    (reduce coerce-bool m #{:pedantic :sanitize :smartLists :smartypants})))

(defn- map->opts
  "Takes a input options map and turns it into a proper object for javax.script"
  [opts]
  (let [{:keys [github-flavored-markdown smart-lists smartypants pedantic sanitize]
         :or {github-flavored-markdown false smart-lists false smartypants false
              pedantic true sanitize false}}
        opts]
    (into {:gfm         (boolean github-flavored-markdown)
           :pedantic    pedantic
           :sanitize    sanitize
           :smartLists  smart-lists
           :smartypants smartypants}
          (gfm-opts github-flavored-markdown))))

(defn- ->options
  [obj]
  (cond
    (instance? MarkedOptions obj) obj
    (map? obj) (-> obj map->opts coerce-opts map->MarkedOptions)
    :else (throw (ex-info "I don't know what these options mean"
                          {:type ::invalid-options
                           :options obj}))))

(defn new-engine
  "Returns a new javascript engine with the marked code loaded"
  []
  (doto (engine)
    (load-marked)))

(defn marked*
  "Like marked*, but takes an options object not a map"
  [engine md options]
  (.invokeFunction ^Invocable engine "marked" (into-array Object [md options])))

(defn marked
  "Turns markdown code into html"
  ([engine md options]
   (marked* engine md (->options options)))
  ([       md options] (marked (new-engine) md options))
  ([       md        ] (marked md default-options)))
