(ns bart.views.suppl
  (:require [bart.data.db :as db]))

(defn header-comp
  []
  [:div
   [:h1 {:class "title"} "BART Estimated Time of Departures"]]
  )

(defn loading
  []
  [:div {:class "loader"}])

