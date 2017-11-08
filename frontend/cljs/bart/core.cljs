(ns bart.core
  (:require [reagent.core :as r]

            [bart.data.db :as db]
            [bart.utils.fetcher :as fetcher]
            [bart.views.suppl :as suppl-views]
            [bart.views.etds :as etd-views]
            [bart.views.stations :as station-views]
            ))

(defn initialize []
  (etd-views/initialize))

(defn root []
  [:div
   [suppl-views/header-comp]
   [station-views/stations-choice-comp]
   (if @db/refreshing-etds
     ; Either loading indicator (if refreshing) OR ETDs-info
     [suppl-views/loading]
     [etd-views/etds-comp]
       )])

(defn mount-root []
  (r/render [root] (.getElementById js/document "app"))
  (initialize))

(defn ^:export init []
  (fetcher/fetch-stations)
  (mount-root))

