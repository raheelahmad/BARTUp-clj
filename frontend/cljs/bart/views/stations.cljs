(ns bart.views.stations
  (:require [bart.utils.fetcher :as fetcher]
            [bart.data.db :as db]))

(defn station-nearest
  []
  [:button {:on-click fetcher/fetch-nearest-etd :class "button"} "Departures near me!"]
  )

(defn station-select-option
  [station]
  (let [name (:name station)
        abbr (:abbreviation station)
        selected (-> @db/station-etds :station :abbr)]
    [:option {:value abbr} name]))

(defn stations-input
  []
  (let [selected (or (-> @db/station-etds :station :abbr) "")]
    (into
     [:select {:on-change #(fetcher/fetch-station-etd (-> % .-target .-value))
               :value selected}]
     (if-let [stations @db/stations]
       (map station-select-option stations)
       [:option {:value "-"} "-"]
       ))))

(defn stations-choice-comp
  "Component to make a choice between
  nearest and a list of stations"
  []
  [:div
   [station-nearest]
   [:div {:class "standard-margin-div"} "OR"]
   [stations-input]
   ]
  )

