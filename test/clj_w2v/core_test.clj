(ns clj-w2v.core-test
  (:require [clojure.test :refer :all]
            [clj-w2v.core :refer :all]))

(deftest file-exists?-test
  (testing "Function file-exists?")
    (is (= (file-exists? "dataset.zip")
           false)))

(deftest count-words-test
  (testing "Function count-words"
    (is (= (sort-word-counts
             (count-words ["a" "b" "c" "d" "d" "e"]))
           '(["d" 2] ["a" 1] ["b" 1] ["c" 1] ["e" 1])))))

