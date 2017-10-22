(ns bart.views.timeline
  (:require cljsjs.d3
            [reagent.core :as r]))

(defonce viz-state
  (r/atom {:width 300
           :data [{:x 1}
                  {:x 2}
                  {:x 3}]}))

(defn get-width [state]
  (:width @state))
(defn get-height [state]
  (let [width (get-width state)]
    (* 0.8 width)))

;; Timeline-viz

(defn timeline-enter [state]
  (let [data (:data @state)]
    (-> (js/d3.select ".timeline")
        (.selectAll "rect")
        (.data (clj->js data))
        .enter
        (.append "rect")
        )))

(defn timeline-update [state]
  (println "Update timeline")
  (let [width (get-width state)
        height (get-height state)
        data (:data @state)
        data-n (count data)
        rect-height (/ height data-n)
        x-scale (-> js/d3
                    .scaleLinear
                    (.domain #js [0 5])
                    (.range #js [0 width])
                    )]
    (-> (js/d3.select ".timeline")
        (.selectAll "rect")
        (.data (clj->js data))
        (.attr "fill" "steelblue")
        (.transition js/d3.transition)
        (.attr "x" (x-scale 0))
        (.attr "y" (fn [_ i] (inc (* i rect-height))))
        (.attr "height" (- rect-height 10))
        (.attr "width" (fn [d]
                         (x-scale (aget d "x"))))
        )))

(defn timeline-exit [state]
  (println "Exiting timeline")
  (let [data (:data @state)]
    (-> (js/d3.select ".timeline")
        (.selectAll "rect")
        (.data (clj->js data))
        .exit
        .remove
        )))

(defn timeline-did-update [state]
  (timeline-enter state)
  (timeline-update state))

(defn timeline-did-mount [state]
  (-> (js/d3.select "svg")
      (.append "g")
      (.attr "class" "timeline"))
  (timeline-did-update state))

;; Container

(defn container-enter [state]
  (-> (js/d3.select "svg")
      (.append "g")
      (.attr "class" "container")))

(defn container-did-mount [state]
  (container-enter state))

;; Component

(defn viz-did-mount [state]
  (container-did-mount state)
  (timeline-did-mount state)
  )
(defn viz-did-update [state]
  (timeline-did-update state)
  )

(defn viz-render
  ;; Renders initial SVG
  [state]
  [:div {:id "viz"}
   [:svg {:width (get-width state) :height (get-height state)}]
   ])

(defn viz [viz-state]
  (r/create-class
   {
    :display-name "ETD timeline"

    :component-did-mount #(viz-did-mount viz-state)
    :component-did-update #(viz-did-update viz-state)

    :reagent-render #(viz-render viz-state)
    }
  ))

;; Overall container

(defn timeline []
    (fn []
      [:div {:class "column is-one-third"}
       [:h2 "Timeline"]
       [:button {:on-click #(swap! viz-state update
                                   :data (fn [_]
                                           [{:x 8}
                                            {:x 9}
                                            {:x 4}
                                            {:x 6}
                                            ]))}
        "Randomize"
        ]
       [viz viz-state]])
    )

