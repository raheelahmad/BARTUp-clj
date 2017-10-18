(ns bart.core
  (:require [reagent.core :as r]

            [bart.data.db :as db]
            [bart.utils.fetcher :as fetcher]
            [bart.views.etds :as etd-views]
            [bart.views.stations :as station-views]
            ))

(defn header-comp
  []
  [:div
   [:h1 {:class "title"} "BART Estimated Time of Departures"]
   [:hr]]
  )

(defn root []
  [:div
   [header-comp]
   (if-let [etds-info @db/station-etds]
     [:div
      [etd-views/etd-station-header (:station etds-info)]
      [:hr]
      [etd-views/etds-comp (:etds etds-info)]]
     [station-views/stations-choice-comp]
     )])

(defn mount-root []
  (r/render [root] (.getElementById js/document "app")))

(defn ^:export init []
  (fetcher/fetch-stations)
  (mount-root))

