(ns bart.views.suppl
  (:require [bart.data.db :as db]))

(defn header-comp
  []
  [:div {:class "hero"}
   [:div {:class "container"}
    [:h1 {:class "title"} "BART Estimated Time of Departures"]
    [:h4 {:class "subtitle"} "Choose by a station near you or pick any one."
     ]]]
  )

(defn loading
  []
  [:div {:class "loader"}])

