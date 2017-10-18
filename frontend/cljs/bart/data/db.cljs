(ns bart.data.db
  (:require [reagent.core :as r]))

(def etd-refresh-interval 18000)

(defonce stations (r/atom nil))
(defonce station-etds (r/atom nil))
