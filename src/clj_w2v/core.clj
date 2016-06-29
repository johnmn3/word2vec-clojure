(ns clj-w2v.core
  (:import
    (java.io FileInputStream FileNotFoundException IOException File BufferedReader InputStreamReader)
    (java.util.zip ZipInputStream ZipEntry))
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.core.reducers :as r])
  (:use [clojure.string]
        [uncomplicate.neanderthal core native]))


(def url "http://mattmahoney.net/dc/")
(def fname "dataset.zip")

(defn download
  [uri file]
  (try
    (with-open [in (io/input-stream (str/join [url uri]))
                out (io/output-stream file)]
    (io/copy in out))
    (catch Exception _
      (do
        (println "Can't connect the server.")
        nil))))

(defn file-exists?
  [filename]
  (.exists (io/as-file filename)))

(defn read-data
  [filename]
  (with-open [zis (ZipInputStream. (io/input-stream filename))]
    (let [br (BufferedReader. (InputStreamReader. zis))
          ze (.getNextEntry zis)]
      (apply str (line-seq br)))))

(defn split-words
  [#^String text]
  (str/split text #"\s"))

(defn count-words
  [words]
  (reduce (fn [counter e]
            (assoc counter e (inc (get counter e 0))))
          {}
          words))

(definline comp-word-counts
  [c1 c2]
  `(if (> (val ~c1) (val ~c2))
    true
    false))

(defn sort-word-counts
  [l]
  (sort (comp comp-word-counts) l))

(defn find-val
  [src value none]
  (loop [v src]
    (if (empty? v)
      none
      (if (= (val (first v)) value)
        (key (first v))
        (recur (rest v))))))

(defn reverse-map
  [m]
  (into {} (map (fn [x] {(val x) (key x)}) m)))


(defn build-dataset
  [words]
  (let [cnt (concat {"UNK" -1}
                    (sort-word-counts (count-words words)))]
    (println "*debug* Counted words")
    (let [dict (into {} (map-indexed array-map (keys cnt)))]
      (println "*debug* Successful building the dictionary")
      (let [data (map #(find-val dict % 0) words)]
        (println "*debug* Complete covert text to vector")
        (let [r-dict (reverse-map dict)]
          {:data data :cnt cnt :dict dict :r-dict r-dict})))))

(def data-index 0)

(defn generate-batch
  [batch-size num-skips skip-window]
  (assert (= (mod batch-size num-skips) 0))
  (assert (<= num-skips (* 2 skip-window))))

(defn -main
  []
  (if-not (file-exists? fname)
    (do
      (println "Dataset is not found.")
      (println "Download it now...")
      (if (nil? (download "text8.zip" fname))
        (System/exit -1)
        (println "The download is completed."))))
  (println "Begin to build the data set.")
  (let [words (split-words (trim (read-data fname)))]
    (println "Data size" (count words))
    (let [dataset (build-dataset words)]
      (println "Most common words (+UNK)" (take 5 (:cnt dataset)))
      (println "Sample data"
               (take 10 (:data dataset))
               (map (fn [x] (get (:dict dataset) x))
                    (take 10 (:data dataset)))))))

