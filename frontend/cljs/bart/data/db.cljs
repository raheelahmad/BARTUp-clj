(ns bart.data.db
  (:require [reagent.core :as r]))

(def etd-refresh-interval 30000)

(defonce source-choice (r/atom nil))

(defonce stations (r/atom nil))
(defonce station-etds (r/atom nil))

(defn is-by-station-abbr []
  (= @source-choice :by-station-abbr))

(defn is-by-nearest []
  (= @source-choice :by-nearest))
