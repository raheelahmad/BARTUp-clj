(ns bart.data.db
  (:require [reagent.core :as r]))

(def etd-refresh-interval 12000)
(def refreshing-etds (r/atom false))

(def repeating (atom false))

(defonce hovering-line (r/atom nil))

;; The three states
(defonce stations (r/atom nil))
(defonce station-etds (r/atom nil))
(defonce source-choice (r/atom nil))

(defn is-by-station-abbr []
  (= @source-choice :by-station-abbr))

(defn is-by-nearest []
  (= @source-choice :by-nearest))

(defn nearest-station-abbr
  []
  (if (is-by-nearest)
    (get-in @station-etds [:station :abbr])))

