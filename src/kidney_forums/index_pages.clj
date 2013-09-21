;;;; Soli Deo Gloria

(ns kidney-forums.index-pages
  (:gen-class :main true)
  (:require [warc-clojure.core :as warc]
            [clojure.tools.cli :as cli]
            [net.cgrand.enlive-html :as html]))

(defn get-post-pages
  [warc-file]
  (doseq [record (warc/skip-get-response-records-seq
                  (warc/get-warc-reader warc-file))]
    (filter
     identity
     (map
      #(-> %
           :attrs
           :href)
      (-> record
          :payload-stream
          html/html-resource
          (html/select [:a]))))))

(defn -main
  [& args]
  (let
      [[_ [warcs-dir] _] (cli/cli args)]

    (doseq [warc-file (filter
                       #(and (re-find #".warc.gz" (.getAbsolutePath %))
                             (not (re-find #"latest" (.getAbsolutePath %))))
                       (file-seq (java.io.File. warcs-dir)))]
      (println (get-post-pages warc-file)))))
