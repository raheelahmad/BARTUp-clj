(ns bart.views.viz.utils)

(defn get-width [state]
  (:width state))
(defn get-height [state]
  (:height state))

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
  [app-state view-state]
  (let [minutes (or (->> app-state :etds all-minutes (map :minutes))
                     '(30)) ;; set a default minute so we avoid nil
        max-pos-minute (apply max minutes)
        min-minute (apply min minutes) ;; possibly a negative
        max-value (max max-pos-minute (js/Math.abs min-minute))
        height (get-height view-state)
        y-inset 20]
    (-> js/d3
        .scaleLinear
        (.domain #js [(- max-value) max-value])
        (.range #js [y-inset (- height y-inset)])
        )))

