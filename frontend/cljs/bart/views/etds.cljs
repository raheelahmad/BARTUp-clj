(ns bart.views.etds
  (:require [ajax.core :as ajax]
            [reagent.core :as r]
            [bart.data.db :as db]
            [bart.views.viz.timeline :as timeline]))

(defn etd-station-header
  "Heading component for a given station"
  [station]
  [:div {:class "station-header"}
   [:span "Trains arriving at "]
   [:span {:class "station-name"} (:name station)]
   [:span " station"] ])

(defn line-comp
  "Component for a single line: an li with the minutes to arrival"
  [line-info]
  (let [name (:destination line-info)
        minutes (apply str (interpose ", " (:minutes line-info)))
        name-comp [:span {:class "line-listing-name"
                          :style { :background-color (:color line-info)}} name]
        minutes-comp [:span (str " in " minutes)]
        ]
    [:li name-comp minutes-comp]))

(defn direction-comp
  "Component for all lines going in a direction"
  [{:keys [direction lines]}]
  [:div {:class "direction"}
   [:h4 direction]
   (into [:ul]
         (map line-comp lines))])

(defn etds-listing-comp [etds-info]
  "Component for all the listed ETDs"
  [:div {:class "column"}
   [:h2 "Listing"]
   [etd-station-header (:station etds-info)]
   (into [:div]
         (map direction-comp (:etds etds-info)))
   ])

(defn etds-comp
  []
  (if-let [etds-info @db/station-etds]
    [:div {:class "columns"}
     [timeline/timeline etds-info]
     [etds-listing-comp etds-info]
     ])
  )

