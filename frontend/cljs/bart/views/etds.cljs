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
  (fn [line-info]
    (let [name (:destination line-info)
          minutes (apply str (interpose ", " (:minutes line-info)))
          if-hovered-class (if (= @db/hovering-line name) "highlighted-line" "")]
      ^{:key name}
      [:li {:class if-hovered-class
            :on-mouse-over #(reset! db/hovering-line name)
            :on-mouse-out #(reset! db/hovering-line nil)}
       [:span {:class "line-listing-name"
               :style { :background-color (:color line-info)}} name]
       [:span (str " " minutes)]])))

(defn direction-comp
  "Component for all lines going in a direction"
  [{:keys [direction lines]}]
  (fn [{:keys [direction lines]}]
    (if (empty? lines)
      [:div (str "No trains going " direction)]
      [:div {:class "direction"}
       [:h4 (str direction " bound")]
       [:ul
        (doall
         (for [line lines]
           ^{:key (:destination line)} [line-comp line]))]])))

(defn etds-listing-comp [etds-info]
  "Component for all the listed ETDs"
  (fn [etds-info]
    [:div {:class "column"}
     [etd-station-header (:station etds-info)]
     [:div
      (for [etds (:etds etds-info)]
        ^{:key (:direction etds)} [direction-comp etds])]
     ]))

(defn etds-comp
  []
  (if-let [etds-info @db/station-etds]
    [:div {:class "columns is-vcentered"}
     [timeline/timeline etds-info]
     [etds-listing-comp etds-info]
     ]))

