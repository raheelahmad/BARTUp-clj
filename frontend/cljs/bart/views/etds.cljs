(ns bart.views.etds
  (:require [ajax.core :as ajax]
            [reagent.core :as r]
            [bart.data.db :as db]
            [bart.views.viz.timeline :as timeline]))

(defn initialize []
  (timeline/initialize)
  )

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
          is-inverted-line (some #{name} ["SFO/Millbrae" "Pittsburg/Bay Point"])
          if-hovered-class (if (= @db/hovering-line name)
                             (if is-inverted-line "highlighted-line-inverted" "highlighted-line" )
                             "")]
      ^{:key name}
      [:li {:class "line-listing"
            :on-mouse-over #(reset! db/hovering-line name)
            :on-mouse-out #(reset! db/hovering-line nil)}
       [:span {:class (str "line-listing-name" " " if-hovered-class)
               :style { :background-color (:color line-info)}} name]
       [:span (str " in " minutes)]])))

(defn direction-comp
  "Component for all lines going in a direction"
  [{:keys [direction lines]}]
  (fn [{:keys [direction lines]}]
    (if (empty? lines)
      [:div (str "No trains going " direction)]
      [:div {:class "direction"}
       [:h4 (.toUpperCase direction)
        [:span {:class "bound"} "bound"]]
       [:ul {:class "direction-list"}
        (doall
         (for [line lines]
           ^{:key (:destination line)} [line-comp line]))]])))

(defn etds-listing-comp [etds-info]
  "Component for all the listed ETDs"
  (fn [etds-info]
    [:div {:class "column"}
     [:div
      (for [etds (->> etds-info
                     :etds
                     (sort-by :direction)
                     reverse)]
        ^{:key (:direction etds)} [direction-comp etds])]
     ]))

(defn etds-comp
  []
  (if-let [etds-info @db/station-etds]
    [:div {:class "arrivals"}
     [etd-station-header (:station etds-info)]
     [:div {:class "columns"}
      [timeline/timeline etds-info]
      [etds-listing-comp etds-info]
      ]]))

