(defproject clj_w2v "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :profiles {:dev {:global-vars {*warn-on-reflection* true}}}
  :jvm-opts ["-Xmx2G"]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [incanter "1.5.7"]
                 ]
  :main clj-w2v.core)
