(ns bart.core
  (:require [reagent.core :as r]
            [ajax.core :as ajax]

            [bart.utils.location :as location]

            [bart.data.db :as db]

            [bart.views.etds :as etd-views]
            [bart.views.stations :as station-views]
            ))

(def fetch-etd-handler
  {:response-format (ajax/json-response-format {:keywords? true})
   :handler (fn [response]
              (reset! db/nearest-station-etds response)
              )})

(defn fetch-etd [lat long]
  (if-let [station (get-in @(db/nearest-station-etds) [:station :abbr])]
    (ajax/GET (str "/station-etd/" station) fetch-etd-handler)
    (ajax/GET (str "/etd/" lat "/" long) fetch-etd-handler)
    ))

(defn show-nearest-etd []
  (location/get-location
   (fn [found-station-coords]
     (let [coords (.-coords found-station-coords)
           lat (.-latitude coords) long (.-longitude coords)]
       (js/setInterval #(fetch-etd lat long) db/etd-refresh-interval)
       (fetch-etd lat long)
     ))))


(defn root []
  [:div
   [:h1 {:class "title"} "BART Estimated Time of Departures"]
   [:hr]
   (if-let [etds-info @(db/nearest-station-etds)]
     [:div
      [station-views/station-comp (:station etds-info)]
      [:hr]
      [etd-views/etds-comp (:etds etds-info)]]
     [:button {:on-click show-nearest-etd :class "button"} "Departures near me"])])

(defn mount-root []
  (r/render [root] (.getElementById js/document "app")))

(defn ^:export init []
  (mount-root))

