(ns bart.utils.fetcher
  (:require [bart.data.db :as db]
            [bart.utils.location :as location]

            [ajax.core :as ajax]))

(defn fetch-etd [lat long]
  (let [fetch-etd-handler {:response-format (ajax/json-response-format {:keywords? true})
                           :handler (fn [response]
                                      (reset! db/station-etds response)
                                      )}]
    (if-let [station (get-in @db/station-etds [:station :abbr])]
      (ajax/GET (str "/station-etd/" station) fetch-etd-handler)
      (ajax/GET (str "/etd/" lat "/" long) fetch-etd-handler)
      )))

(defn show-nearest-etd []
  (location/get-location
   (fn [found-station-coords]
     (let [coords (.-coords found-station-coords)
           lat (.-latitude coords) long (.-longitude coords)]
       (js/setInterval #(fetch-etd lat long) db/etd-refresh-interval)
       (fetch-etd lat long)
       ))))

(defn fetch-stations []
  (ajax/GET "/stations" {:response-format (ajax/json-response-format {:keywords? true})
                         :handler (fn [response]
                                    (reset! db/stations response))}
            ))
