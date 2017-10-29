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

(def etds-catch [{:direction "North",
  :lines
  [{:destination "Richmond",
    :direction "North",
    :minutes ["12" "26" "46"]}
   {:destination "Daly City",
    :direction "North",
    :minutes ["21" "41"]}]}
 {:direction "South",
  :lines
  [{:destination "Warm Springs",
    :direction "South",
    :minutes ["2" "20" "40"]}]}])

(def richmond
  {:destination "Richmond",
   :direction "North",
   :minutes ["12" "26" "46"]})

(defn line-minutes [{:keys [destination minutes direction]}]
  (reduce #(conj %1 {:minutes (if (= direction "South") (- (int %2)) (int %2))
                     :destination destination
                     :direction direction})
          [] minutes)
  )

(defn direction-minutes [{:keys [lines direction]}]
  (map line-minutes lines)
  )

(defn north-south-minutes
  [etds]
  (let [[north south] (map direction-minutes etds)]
    (concat (->> north (sort-by :minutes))
            ;; (->> south  sort (map -))
            (->> south (sort-by :minutes))
            )
    ))

(defn all-minutes [etds]
  (-> etds north-south-minutes flatten)
  )

(defn y-range
  [etds view-state]
  (let [minutes (map :minutes (flatten (north-south-minutes etds)))
        max-minute (apply max minutes)
        min-minute (apply min minutes)
        height (get-height view-state)
        ]
    ;; (println (str "All minutes: " minutes))
    ;; (println (str "max: " max-minute ", min: " min-minute))
    (-> js/d3
        .scaleLinear
        (.domain #js [min-minute max-minute])
        (.range #js [0 height])
        )))

(defn timeline-enter [app-state view-state]
  (let [etds (:etds app-state)
        minutes (all-minutes etds) ]
    (println (str "Minutes data for enter: " minutes))
    (-> (js/d3.select ".timeline")
        (.selectAll "rect")
        (.data (clj->js minutes))
        .enter
        (.append "rect")
        )
    ))

(defn timeline-update [app-state view-state]
  (let [etds (:etds app-state)
        y-scale (y-range etds view-state)
        data (all-minutes etds)
        _ (println (str "y domain" (.domain y-scale)))
        data-n (count data)
        _ (println (str data-n  " Minutes data for update: " data))
        eg-minute (:minutes (last data))
        _ (println (str "y for " eg-minute ": " (y-scale eg-minute)))
        ]
    (-> (js/d3.select ".timeline")
        (.selectAll "rect")
        (.data (clj->js data))
        (.attr "x" 10)
        (.attr "y" (fn [d _] (y-scale (:minutes d))))
        (.attr "height" 5)
        (.attr "width" 5)
        )))

(defn timeline-exit [app-state]
  (println "Exiting timeline")
  (let [etds (:etds app-state)
        data (north-south-minutes etds)]
    (-> (js/d3.select ".timeline")
        (.selectAll "rect")
        (.data (clj->js data))
        .exit
        .remove
        )))

(defn timeline-did-update [app-state]
  (timeline-enter app-state @viz-state)
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

