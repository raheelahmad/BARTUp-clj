(ns bart.views.stations
  (:require [bart.utils.fetcher :as fetcher]
            [bart.data.db :as db]))

(defn checked []
  [:span {:class "horizontal-spacer" :style {:color "steelblue"}} "•"]
  )

;; -- Component for nearest station selector
(defn by-station-nearest
  []
  [:div
   [:button {:on-click fetcher/fetch-nearest-etd
             :class "button"} "Departures near me!"]
   (if (db/is-by-nearest) [checked])
   ])

;; -- Component for stations drop-down selector
(defn station-select-option
  [station]
  (let [name (:name station)
        abbr (:abbreviation station)
        selected (-> @db/station-etds :station :abbr)]
    [:option {:value abbr} name]))
(defn by-stations-input
  []
  (let [selected (or (-> @db/station-etds :station :abbr) "")]
    [:div
     [:span {:class "select"}
      (into
       [:select {:on-change #(fetcher/fetch-station-etd (-> % .-target .-value))
                 :value selected}]
       (if-let [stations @db/stations]
         (map station-select-option stations)
         [:option {:value "-"} "-"]
         ))]
     (if (db/is-by-station-abbr) [checked])
     ]))

(defn stations-choice-comp
  "Component to make a choice between
  nearest and a list of stations"
  []
  [:div
   [by-station-nearest]
   [:div {:class ".or-separator"} "OR"]
   [by-stations-input]
   ]
  )

