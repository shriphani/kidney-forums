(ns kidney-forums.core
  (:require [itsy.core :as itsy]
            [net.cgrand.enlive-html :as html]
            [clojure.data.json :as json]
            [clojure.tools.cli :as cli]
            (org.bovinegenius [exploding-fish :as uri])))

(def *kidney-space-selector* [:a.navPages])
(def *davita-selector* [:div.threadpagenav :a])
(def *healthboards-selector* [:tbody :tr :td :a.smallfont])

(defn kidney-space-extractor
  [url body]
  (map
   #(->> %
         :attrs
         :href
         (uri/resolve-uri url))
   (html/select
    (html/html-resource (java.io.StringReader. body))
    *kidney-space-selector*)))

(defn make-kidney-space-handler
  [output-file]
  (fn [{:keys [url body]}]
    (with-open [out (clojure.java.io/writer output-file :append true)]
      (binding [*out* out]
        (clojure.pprint/pprint (json/write-str {:url url
                                                :body body}))
        (flush)))))

(defn -main
  [& args]
  (let [[_ [seed output-file] _] (cli/cli args)]
    (itsy/crawl {:url seed
                 :handler (make-kidney-space-handler output-file)
                 :workers 1
                 :url-limit 30
                 :url-extractor kidney-space-extractor
                 :host-limit true})))
