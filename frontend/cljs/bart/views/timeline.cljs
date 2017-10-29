(ns bart.views.timeline
  (:require cljsjs.d3
            [reagent.core :as r]
            [bart.data.db :as db]))

(defonce viz-state
  (r/atom {:width 220}))

(defn get-width [state]
  (:width state))
(defn get-height [state]
  (let [width (get-width state)]
    (* 1.8 width)))

;; Timeline-viz

(defn line-minutes [{:keys [destination minutes direction]}]
  (reduce #(conj %1 {:minutes (if (= direction "South") (- (int %2)) (int %2))
                     :destination destination
                     :direction direction})
          [] minutes))

(defn direction-minutes [{:keys [lines direction]}]
  (map line-minutes lines)
  )

(defn north-south-minutes
  [etds]
  (let [[north south] (map direction-minutes etds)]
    (concat (->> north (sort-by :minutes))
            ;; (->> south  sort (map -))
            (->> south (sort-by :minutes))
            )))

(defn all-minutes [etds]
  (-> etds north-south-minutes flatten))

(defn y-range
  [etds view-state]
  (let [minutes (map :minutes (flatten (north-south-minutes etds)))
        max-minute (apply max minutes)
        min-minute (apply min minutes)
        height (get-height view-state)
        y-buffer 10]
    (-> js/d3
        .scaleLinear
        (.domain #js [min-minute max-minute])
        (.range #js [y-buffer (- height y-buffer)])
        )))

(defn timeline-enter [app-state]
  (let [etds (:etds app-state)
        minutes (all-minutes etds)]
    (-> (js/d3.select ".timeline")
        (.selectAll "circle")
        (.data (clj->js minutes))
        .enter
        (.append "circle")
        (.attr "cx" 10)
        (.attr "r" 6)
        (.style "fill" "steelblue")
        )
    ))

(defn timeline-update [app-state view-state]
  (let [etds (:etds app-state)
        y-scale (y-range etds view-state)
        data (all-minutes etds)]
    (-> (js/d3.select ".timeline")
        (.selectAll "circle")
        (.attr "cy" (fn [d _]
                     (y-scale (.-minutes d))
                     )))))

(defn timeline-exit [app-state]
  (println "Exiting timeline")
  (let [etds (:etds app-state)
        data (all-minutes etds)]
    (-> (js/d3.select ".timeline")
        (.selectAll "circle")
        (.data (clj->js data))
        .exit
        .remove
        )))

(defn timeline-did-update [app-state]
  (timeline-enter app-state)
  (timeline-update app-state @viz-state)
  (timeline-exit app-state)
  )

(defn timeline-did-mount [state]
  (-> (js/d3.select ".viz")
      (.append "g")
      (.attr "class" "timeline"))
  (timeline-did-update state)
  )

;; Container

(defn container-enter []
  (-> (js/d3.select "svg")
      (.append "g")
      (.attr "class" "viz")))

(defn container-did-mount []
  (container-enter))

;; Component

(defn viz-did-mount [state]
  (container-did-mount)
  (timeline-did-mount state)
  )

(defn viz-did-update []
  (timeline-did-update @db/station-etds)
  )

(defn viz-render
  ;; Renders initial SVG
  [state]
  [:div {:id "viz"}
   [:svg {:width (get-width @state) :height (get-height @state)}]
   ])

(defn viz [state]
  (r/create-class
   {
    :display-name "ETD timeline"

    :component-did-mount #(viz-did-mount state)
    :component-did-update #(viz-did-update)

    :reagent-render #(viz-render viz-state)
    }
  ))

;; Overall container

(defn timeline [state]
    (fn [state]
      [:div {:class "column is-one-third"}
       [:h2 "Timeline"]
       [viz state]])
    )

