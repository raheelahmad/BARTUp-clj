(ns bart.core
  (:require [reagent.core :as r]

            [bart.data.db :as db]
            [bart.views.etds :as etd-views]
            [bart.views.stations :as station-views]
            ))

(defn root []
  [:div
   [:h1 {:class "title"} "BART Estimated Time of Departures"]
   [:hr]
   (if-let [etds-info @db/nearest-station-etds]
     [:div
      [station-views/station-header (:station etds-info)]
      [:hr]
      [etd-views/etds-comp (:etds etds-info)]]
     [:button {:on-click show-nearest-etd :class "button"} "Departures near me"])])

(defn mount-root []
  (r/render [root] (.getElementById js/document "app")))

(defn ^:export init []
  (mount-root))

