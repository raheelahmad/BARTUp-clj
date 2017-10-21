(ns bart.views.etds
  (:require [ajax.core :as ajax]
            [reagent.core :as r]
            [bart.data.db :as db]))

(defn etd-station-header
  "Heading component for a given station"
  [station]
  [:div (str "Trains arriving at " (:name station) " station")])

(defn line-comp
  "Component for a single line: an li with the minutes to arrival"
  [line-info]
  (let [name (:destination line-info)
        minutes (apply str (interpose ", " (:minutes line-info)))
        ]
    [:li (str name " arrives in " minutes)]))

(defn direction-comp [{:keys [direction lines]}]
  [:div
   [:h4 direction]
   (into [:ul]
         (map line-comp lines))])

(defn line-etds-comp [etds]
  (into [:div]
        (map direction-comp etds)))

(defn etds-comp
  [etds-info]
  [:div
   [etd-station-header (:station etds-info)]
   [line-etds-comp (:etds etds-info)]])

