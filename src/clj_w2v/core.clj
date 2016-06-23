(ns clj-w2v.core
  (:import
    (java.io FileInputStream FileNotFoundException IOException File BufferedReader InputStreamReader)
    (java.util.zip ZipInputStream ZipEntry))
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.core.reducers :as r])
  (:use [clojure.string]))


(def url "http://mattmahoney.net/dc/")
(def fname "dataset.zip")
(def max-size 10000)

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

;(download "text8.zip" fname)

(defn read-data
  [filename]
  (with-open [zs (ZipInputStream.
                   (io/input-stream filename))]
    (let [ze (.getNextEntry zs)]
      (let [size (.getSize ze)]
        (let [bytes (byte-array max-size)
              res ""]
          (loop [offset 0
                 remain max-size]
            (if (<= max-size offset)
              (str/join [res
                         " "
                         (trimr (String. bytes "UTF-8"))])
              (do
                (if (< remain 1024)
                  (do
                    (.read zs bytes offset remain)
                    (str/join [res
                               " "
                               (trimr (String. bytes "UTF-8"))]))
                  (do
                    (.read zs bytes offset 1024)
                    (recur (+ offset 1024) (- remain 1024))))))))))))

(defn read-da
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

(defn reverse-map-old
  [m]
  (apply array-map (interleave (vals m) (keys m))))

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
      (let [;data (loop [s words
            ;            d (transient [])]
            ;       (if (empty? s)
            ;         (persistent! d)
            ;         (recur (rest s) (conj! d (find-val dict (first s) 0)))))]
            data (map #(find-val dict % 0) words)]
        (println "*debug* Complete covert text to vector")
        (let [r-dict (reverse-map dict)]
          {:data data :cnt cnt :dict dict :r-dict r-dict})))))

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
  (let [words (split-words (trim (read-da fname)))]
    (println "Data size" (count words))
    (let [dataset (build-dataset words)]
      (println "Most common words (+UNK)" (take 5 (:cnt dataset)))
      (println "Sample data"
               (take 10 (:data dataset))
               (map (fn [x] (get (:dict dataset) x))
                    (take 10 (:data dataset)))))))

;dict
;(def an (build-dataset ["one" "two" "two" "three" "three"]))


;(print (read-file "/home/tamamu/text8.zip"))
;(def x (trim (apply str (read-data "/home/tamamu/text8.zip"))))
;(def w (split-words x))
;(def ds (build-dataset w))
;(= (get (:dict ds) (first (:data ds))) (first w))
