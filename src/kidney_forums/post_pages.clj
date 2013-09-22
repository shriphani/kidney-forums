;;;; Soli Deo Gloria

(ns kidney-forums.post-pages
  (:gen-class :main true)
  (:require [clojure.data.json :as json]
            [clojure.tools.cli :as cli]
            [warc-clojure.core :as warc]))

(defn handle-warc-file
  [warc-file]
  (doseq [record (filter
                  #(and (not
                         (re-find #"^dns:" (:target-uri-str %)))
                        (not
                         (re-find #"robots.txt$" (:target-uri-str %))))
                  (warc/get-response-records-seq
                   (warc/get-warc-reader warc-file)))]
    (println (json/write-str (merge
                              record {:payload        (slurp
                                                       (:payload-stream record))
                                      :content-type   (.toString
                                                       (:content-type record))
                                      :date           (.toString
                                                       (:date record))
                                      :target-uri     (:target-uri-str record)
                                      :payload-stream nil})))
    (flush)))

(defn -main
  [& args]
  (let [[_ [warc-file] _] (cli/cli args)]
    (handle-warc-file warc-file)))
