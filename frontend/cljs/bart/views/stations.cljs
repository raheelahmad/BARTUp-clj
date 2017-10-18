(ns bart.views.stations)

(defn station-comp
  "Heading component for a given station"
  [station]
  [:div (str "Trains arriving at " (:name station) " station")])

