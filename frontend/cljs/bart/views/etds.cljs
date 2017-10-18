(ns bart.views.etds
  (:require [ajax.core :as ajax]
            [reagent.core :as r]))

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

(defn etds-comp [etds]
  (into [:div]
        (map direction-comp etds)))

