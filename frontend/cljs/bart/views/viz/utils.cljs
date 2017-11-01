(ns bart.views.viz.utils)

(defn get-width [state]
  (:width state))
(defn get-height [state]
  (let [width (get-width state)]
    (* 1.5 width)))

(defn line-minutes [{:keys [destination minutes direction color]}]
  (reduce #(conj %1 {:minutes (if (= direction "South") (- (int %2)) (int %2))
                     :destination destination
                     :color color
                     :direction direction})
          [] minutes))

(defn direction-minutes [{:keys [lines direction]}]
  (map line-minutes lines)
  )

(defn north-south-minutes
  [etds]
  (let [[north south] (map direction-minutes etds)]
    (concat (->> north (sort-by :minutes))
            (->> south (sort-by :minutes))
            )))

(defn all-minutes [etds]
  (-> etds north-south-minutes flatten))

(defn y-scale
  [view-state]
  (let [height (get-height view-state)
        y-buffer 10]
    (-> js/d3
        .scaleLinear
        (.domain #js [-60 60])
        (.range #js [y-buffer (- height y-buffer)])
        )))

