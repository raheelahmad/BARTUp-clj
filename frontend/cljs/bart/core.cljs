(ns bart.core
  (:require [reagent.core :as r]
            [bart.views.etds :as etds]
            [bart.utils.location :as location]
            [ajax.core :as ajax]))

(def etd-refresh-interval 18000)

(defonce nearest-station-etds (r/atom nil))

(def fetch-etd-handler
  {:response-format (ajax/json-response-format {:keywords? true})
   :handler (fn [response]
              (reset! nearest-station-etds response)
              )})

(defn fetch-etd [lat long]
  (if-let [station (get-in @nearest-station-etds [:station :abbr])]
    (ajax/GET (str "/station-etd/" station) fetch-etd-handler)
    (ajax/GET (str "/etd/" lat "/" long) fetch-etd-handler)
    ))

(defn show-nearest-etd []
  (location/get-location
   (fn [found-station-coords]
     (let [coords (.-coords found-station-coords)
           lat (.-latitude coords) long (.-longitude coords)]
       (js/setInterval #(fetch-etd lat long) etd-refresh-interval)
       (fetch-etd lat long)
     ))))


(defn root []
  [:div
   [:h1 {:class "title"} "BART Departures near you"]
   [:hr]
   (if-let [etds-info @nearest-station-etds]
     [:div
      [etds/station-comp (:station etds-info)]
      [:hr]
      [etds/etds-comp (:etds etds-info)]]
     [:button {:on-click show-nearest-etd :class "button"} "Departures near me"])])

(defn mount-root []
  (r/render [root] (.getElementById js/document "app")))

(defn ^:export init []
  (mount-root))

