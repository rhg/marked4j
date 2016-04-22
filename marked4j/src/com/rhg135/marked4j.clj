(ns com.rhg135.marked4j
  "Process markdown using https://github.com/chjj/marked using rhino/nashorn.

  Example usage: (com.rhg135.marked4j/marked \"so here's stuff\")
   An optional options map can be passed last, for details see `com.rhg135.marked4j/*options*'"
  (:require [clojure.java.io :as io])
  (:import [java.io FileReader File]
           [javax.script ScriptEngineManager ScriptEngine Invocable]))

(def ^:dynamic *options* ; TODO: figure out how to get renderers and highlighters working
  "The options to use if none are supplied explicitly.
    :concurrent? - controls if the interactions are through an agent - True." ; TODO: document options
  {:concurrent? true
   :github-flavored-markdown {:tables? false :breaks? false}
   :pedantic true
   :sanitize-output-and-ignore-html? true
   :fancy-typography? false
   :marked/smartLists false})

(defn- gfm-opts
  "Returns gfm-specific options"
  [{:keys [tables? breaks?]}]
  {:gfm (or tables? breaks?)
   :tables tables?
   :breaks breaks?})

(defn- ->options
  "Takes a input options map and turns it into a proper object for javax.script"
  [opts]
  (let [{:keys [github-flavored-markdown :marked/smartLists fancy-typography? pedantic? sanitize-output-and-ignore-html?]}
        opts]
    (reduce-kv (fn [m k _] (update m k boolean))
               {}
               (into {:pedantic    pedantic?
                      :sanitize    sanitize-output-and-ignore-html?
                      :smartLists  smartLists
                      :smartypants fancy-typography?}
                     (gfm-opts github-flavored-markdown)))))

(defn- ^ScriptEngine engine ; TODO: figure out a nice way to expose this
  "Returns an engine for a given name. js by default"
  ([^String name] (.getEngineByName (ScriptEngineManager.) name))
  ([] (engine "javascript")))

(defn- default-marked
  "Return the bundled marked.min.js 0.3.2"
  []
  (io/resource "marked.min.js"))

(defn- load-marked!
  "Evals the marked js"
  ([engine url]
   (.eval engine (slurp url)))
  ([engine] (load-marked! engine (default-marked))))

(defn- invoke-function*
  [engine fn-name & args]
  (.invokeFunction engine (name fn-name) (object-array args)))

(defn- sync-marked
  "Tries to run marked in `engine' and if it throws, wrap it and return an exception."
  [engine md options]
  (try
    (invoke-function* engine 'marked md (->options options))
    (catch Exception e
      (ex-info "Failed to convert markdown to html." {:type ::marked :md md :options options} e))))

(defn- async-marked
  [engine markdown-string options]
  (let [p (promise)]
    (send engine
          (fn [engine]
            (doto engine
              (as-> $ (sync-marked $ markdown-string options) (deliver p $)))))
    p))

(defn- throw-error
  "Throws a throwable, but returns anything else."
  [e]
  (if (instance? Throwable e)
    (throw e)
    e))

(defn- marked-function
  "Returns a function to actually invoke marked."
  [concurrent?]
  (if concurrent?
    (comp throw-error deref async-marked)
    (comp throw-error (fn [x & more] (apply sync-marked @x more)))))

(defn new-handle
  "Returns a handle we can pass to `marked'"
  []
  (agent (doto (engine) (load-marked!))))

(let [marked-engine ; we pretend it will never fail, and if it does, you have bigger problems
                    ; still would be nice to be able to reset this TODO: add a way
                    ; this is the default handle if none is passed to marked
      (delay (new-handle))]
  (defn marked ; TODO: expose an async API
    "Turns markdown code into html"
    ([markdown-string] (marked markdown-string *options*))
    ([markdown-string options] (marked @marked-engine markdown-string options))
    ([handle markdown-string {:keys [concurrent?] :as options}]
     ((marked-function concurrent?)
      handle markdown-string options))))
