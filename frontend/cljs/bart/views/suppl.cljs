(ns bart.views.suppl
  (:require [bart.data.db :as db]))

(defn header-comp
  []
  [:div {:class "hero top-header"}
    [:h1 "BART Estimated Time of Departures"]
    [:h4 "Choose by a station near you or pick any one."
   ]]
  )

(defn loading
  []
  [:div {:class "loader"}])

