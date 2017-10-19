(ns bart.core
  (:require [reagent.core :as r]

            [bart.data.db :as db]
            [bart.utils.fetcher :as fetcher]
            [bart.views.suppl :as suppl-views]
            [bart.views.etds :as etd-views]
            [bart.views.stations :as station-views]
            ))

(defn root []
  [:div
   [suppl-views/header-comp]
   [station-views/stations-choice-comp]
   [:hr]
   (if-let [etds-info @db/station-etds]
     [:div
      [etd-views/etd-station-header (:station etds-info)]
      [etd-views/etds-comp (:etds etds-info)]]
     )])

(defn mount-root []
  (r/render [root] (.getElementById js/document "app")))

(defn ^:export init []
  (fetcher/fetch-stations)
  (mount-root))

