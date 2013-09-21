;;;; Soli Deo Gloria

(ns kidney-forums.index-pages
  (:gen-class :main true)
  (:require [warc-clojure.core :as warc]
            [clojure.tools.cli :as cli]))

(defn -main
  [& args]
  (let
      [[_ [warcs-dir] _] (cli/cli args)]

    (doseq [warc-file (filter
                       #(and (re-find #".warc.gz" (.getAbsolutePath %))
                             (not (re-find #"latest" (.getAbsolutePath %))))
                       (file-seq (java.io.File. warcs-dir)))]
      (println warc-file))))
