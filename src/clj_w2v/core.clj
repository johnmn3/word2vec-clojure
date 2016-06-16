(ns clj-w2v.core)

(def url "http://mattmahoney.net/dc/")

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(defn read-data
  [filename]
  )

(def vocabulary-size 50000)

(sort > (vals {:a 238 :b 12 :c 89 :d 24}))

(defn count-words
  [words]
  (reduce (fn [counter e]
            (assoc counter e (inc (get counter e 0))))
          {}
          words))

(defn comp-word-counts
  [c1 c2]
  (if (> (last c1) (last c2))
    true
    false))

(defn sort-word-counts
  [l]
  (sort (comp comp-word-counts) l))

(defn build-dataset
  [words]
  (let [cnt (sort-word-counts
                (concat {"UNK" -1} (count-words words)))
        dict {}]
    cnt))

;dict
(map-indexed list (keys (build-dataset ["a" "b"  "b" "c"])))
