(ns com.rhg135.marked4j.main
  (:gen-class))

(defn do-main ; FIXME: this doesn't return
  []
  (let [in (slurp *in*)
        _ (require 'com.rhg135.marked4j)
        out ((resolve 'com.rhg135.marked4j/marked) in)]
    (print out)))

(defn do-usage
  []
  (binding [*out* *err*]
    (println "USAGE: java -jar marked4j.jar"))
  (System/exit 2))

(defn -main ; TODO: be able to pass options
  [& args]
  (if (empty? args)
    (do-main)
    (do-usage)))
