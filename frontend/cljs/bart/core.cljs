(ns bart.core
  (:require [reagent.core :as r]
            [bart.views.etds :as etds]
            [bart.utils.location :as location]
            [ajax.core :as ajax]))

(def etd-refresh-interval 12000)
(defonce nearest-station-etds (r/atom nil))

(defn fetch-etd [lat long]
  (ajax/GET (str "/etd/" lat "/" long)
            {:response-format (ajax/json-response-format {:keywords? true})
             :handler (fn [response]
                        (reset! nearest-station-etds response)
                        )}))

(defn show-nearest-etd []
  (location/get-location
   (fn [found-station]
     (let [coords (.-coords found-station)
           lat (.-latitude coords) long (.-longitude coords)]
       (js/setInterval #(fetch-etd lat long) etd-refresh-interval)
       (fetch-etd lat long)
     ))))


(defn root []
  [:div
   [:h1 {:class "title"} "BART Departures near you"]
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

