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

(defn direction-comp [[direction etds]]
  [:div
   [:h4 direction]
   (into [:ul]
         (map line-comp etds))])

(defn etds-comp [etds]
  (into [:div]
        (map direction-comp etds)))

