(ns bart.utils.fetcher
  (:require [bart.data.db :as db]
            [bart.utils.location :as location]

            [ajax.core :as ajax]))

(declare fetch-station-etd)

(defn repeat-if-needed []
  (if (not @db/repeating)
    (js/setInterval (fn []
                      (fetch-station-etd (get-in @db/station-etds [:station :abbr]))
                      (reset! db/repeating true)
                      )
                    db/etd-refresh-interval))
  )

(def fetch-etd-handler
  {:response-format (ajax/json-response-format {:keywords? true})
   :handler (fn [response]
              (reset! db/refreshing-etds false)
              (reset! db/station-etds (assoc response :fetched-at (.getTime (js/Date.))))
              (repeat-if-needed)
              )})

(defn fetch-station-etd [station-abbr]
  (ajax/GET (str "/station-etd/" station-abbr) fetch-etd-handler)
  )

(defn fetch-location-etd [lat long]
  (if-let [station (db/nearest-station-abbr)]
    (ajax/GET (str "/station-etd/" station) fetch-etd-handler)
    (ajax/GET (str "/etd/" lat "/" long) fetch-etd-handler)
    ))

(defn fetch-nearest-etd []
  (reset! db/station-etds nil)
  (location/get-location
   (fn [found-station-coords]
     (let [coords (aget found-station-coords "coords")
           lat (aget coords "latitude") long (aget coords "longitude")]
       (reset! db/source-choice :by-nearest)
       (fetch-location-etd lat long)
       ))))

(defn fetch-stations []
  (ajax/GET "/stations" {:response-format (ajax/json-response-format {:keywords? true})
                         :handler (fn [response]
                                    (reset! db/stations response))}
            ))
