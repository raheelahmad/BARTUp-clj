(ns bart.views.etds
  (:require [ajax.core :as ajax]
            [reagent.core :as r]))

(defn station-comp [station]
  [:div (str "You are at the " (:name station) " station!")])

(defn line-comp [line-info]
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

