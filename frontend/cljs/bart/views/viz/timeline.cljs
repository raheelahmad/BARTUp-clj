(ns bart.views.viz.timeline
  (:require cljsjs.d3
            [reagent.core :as r]
            [bart.data.db :as db]
            [bart.views.viz.lifecycle :as lifecycle]
            [bart.views.viz.utils :as utils]))

(defonce viz-state
  (r/atom {:width 320 :height 700}))

(defn initialize
  "Currently doesn't do anything. An example of mounting components."
  []
)

;; Timeline-viz

(defn timeline-did-update [app-state]
  (lifecycle/timeline-enter app-state)
  (lifecycle/timeline-update app-state @viz-state)
  (lifecycle/timeline-exit app-state)
  )

(defn timeline-did-mount [state]
  (-> (js/d3.select ".viz")
      (.append "g")
      (.attr "class" "timeline"))
  (lifecycle/timeline-mount state @viz-state)
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

(defn viz-did-mount [this state]
  (let [width (-> this
                  r/dom-node
                  .-parentNode
                  .-clientWidth)]
    (swap! viz-state assoc :width width)
    (-> (js/d3.select "svg")
        (.attr "width" (utils/get-width @viz-state))
        (.attr "height" (utils/get-height @viz-state))
        ))
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
   [:svg {:width (utils/get-width @state) :height (utils/get-height @state)}]
   ])

(defn viz [state]
  (r/create-class
   {
    :display-name "ETD timeline"

    :component-did-mount (fn [this] (viz-did-mount this state))
    :component-did-update #(viz-did-update)

    :reagent-render #(viz-render viz-state)
    }
  ))

;; Overall container

(defn timeline [state]
    (fn [state]
      [:div {:class "column is-half"}
       ;; [:h2 "Timeline"]
       [viz state]])
    )

