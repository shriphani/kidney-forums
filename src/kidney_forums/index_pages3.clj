;;;; Soli Deo Gloria

(ns kidney-forums.index-pages3
  (:gen-class :main true)
  (:require [warc-clojure.core :as warc]
            [clojure.tools.cli :as cli]
            [net.cgrand.enlive-html :as html]
            (org.bovinegenius [exploding-fish :as uri])))

(defn get-post-pages
  [warc-file]
  (doseq [record (warc/get-response-records-seq
                  (warc/get-warc-reader warc-file))]
    (doseq [link (distinct
                  (filter
                   #(and %
                         (not (re-find #"\&p=" %))
                         (re-find #"f=27\&m=" %))
                   (map
                    #(-> %
                         :attrs
                         :href)
                    (-> record
                        :payload-stream
                        (html/html-resource)
                        (html/select [:a])))))]
      (println (uri/resolve-uri (:target-uri-str record) link))
      (flush))))

(defn -main
  [& args]
  (let
      [[_ [warcs-dir] _] (cli/cli args)]

    (doseq [warc-file (filter
                       #(and (re-find #".warc.gz" (.getAbsolutePath %))
                             (not (re-find #"latest" (.getAbsolutePath %))))
                       (file-seq (java.io.File. warcs-dir)))]
      (get-post-pages warc-file))))

