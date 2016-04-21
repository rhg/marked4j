(ns com.rhg135.marked4j-test
  (:require [com.rhg135.marked4j :as marked4j]
            [clojure.test :refer (is deftest testing)]))

(defn test-sync-and-async
  [input output]
  (let [opts marked4j/*options*]
    (testing "Unsafe"
      (is (= output (marked4j/marked input (assoc opts :concurrent? false)))))
    (testing "Safe"
      (is (= output (marked4j/marked input opts))))))

(deftest encoding
  (testing "Plain text with default options"
    (test-sync-and-async "xxx" "<p>xxx</p>\n")))
