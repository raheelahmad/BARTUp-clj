(ns bart.views.stations
  (:require [bart.utils.fetcher :as fetcher]
            [bart.data.db :as db]))

(defn checked []
  [:span {:class "horizontal-spacer" :style {:color "steelblue"}} "•"]
  )

;; -- Component for nearest station selector
(defn by-station-nearest
  []
  [:span
   [:button {:on-click (fn [e]
                         (reset! db/refreshing-etds true)
                         (fetcher/fetch-nearest-etd))
             :class "button"} "Departures near me!"]
   (if (db/is-by-nearest) [checked])
   ])

;; -- Component for stations drop-down selector
(defn station-select-option
  [station]
  (let [name (or (:name station) "*")
        abbr (:abbreviation station)
        selected (-> @db/station-etds :station :abbr)]
    [:option {:value abbr} name]))
(defn by-stations-input
  []
  (if-let [selected (-> @db/station-etds :station :abbr)]
    [:span
     [:span {:class "select"}
      (into
       [:select {:on-change (fn [e]
                              (reset! db/refreshing-etds true)
                              (reset! db/source-choice :by-station-abbr)
                              (fetcher/fetch-station-etd (-> e .-target .-value)))
                 :value selected}]
       (if-let [stations @db/stations]
         (map station-select-option stations)
         ))]
     (if (db/is-by-station-abbr) [checked])
     ]))

(defn stations-choice-comp
  "Component to make a choice between
  nearest and a list of stations"
  []
  [:div {:class "station-choice"}
   [by-station-nearest]
   [:span {:class "or-separator"} " or "]
   [by-stations-input]
   ])

