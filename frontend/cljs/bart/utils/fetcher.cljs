(ns bart.utils.fetcher
  (:require [bart.data.db :as db]
            [bart.utils.location :as location]

            [ajax.core :as ajax]))

(def fetch-etd-handler
  {:response-format (ajax/json-response-format {:keywords? true})
   :handler (fn [response]
              (reset! db/station-etds response)
              )})

(defn fetch-station-etd [station-abbr]
  (reset! db/source-choice :by-station-abbr)
  (ajax/GET (str "/station-etd/" station-abbr) fetch-etd-handler)
  )

(defn fetch-location-etd [lat long]
  (if-let [station (get-in @db/station-etds [:station :abbr])]
    (ajax/GET (str "/station-etd/" station) fetch-etd-handler)
    (ajax/GET (str "/etd/" lat "/" long) fetch-etd-handler)
    ))

(defn fetch-nearest-etd []
  (reset! db/station-etds nil)
  (location/get-location
   (fn [found-station-coords]
     (let [coords (.-coords found-station-coords)
           lat (.-latitude coords) long (.-longitude coords)]
       (reset! db/source-choice :by-nearest)
       (js/setInterval #(fetch-location-etd lat long) db/etd-refresh-interval)
       (fetch-location-etd lat long)
       ))))

(defn fetch-stations []
  (ajax/GET "/stations" {:response-format (ajax/json-response-format {:keywords? true})
                         :handler (fn [response]
                                    (reset! db/stations response))}
            ))
