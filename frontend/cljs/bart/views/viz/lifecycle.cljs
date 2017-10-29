(ns bart.views.viz.lifecycle
  (:require cljsjs.d3
            [bart.views.viz.utils :as u]))

(defn timeline-enter [app-state]
  (let [etds (:etds app-state)
        minutes (u/all-minutes etds)
        entered-gs (-> (js/d3.select ".timeline")
                       (.selectAll "g.minute")
                       (.data (clj->js minutes))
                       .enter
                       (.append "g")
                       (.attr "class" "minute"))
        entered-circles (-> entered-gs
                            (.append "circle")
                            (.attr "cx" 10)
                            (.attr "r" 3)
                            )
        entered-text (-> entered-gs
                         (.append "text")
                         (.attr "dx" 19)
                         (.attr "dy" 5)
                         )
        ]))

(defn timeline-update [app-state view-state]
  (let [etds (:etds app-state)
        y-scale (u/y-scale etds view-state)
        data (u/all-minutes etds)
        gs (-> (js/d3.select ".timeline")
               (.selectAll "g.minute"))
        texts (-> gs (.select "text"))
        ]
    (-> gs (.attr "transform"
                  (fn [d _]
                    (let [y (y-scale (.-minutes d))]
                      (str "translate(10, " y ")")))))
    (-> texts
        (.text (fn [d] (.-minutes d))))
        ))

(defn timeline-exit [app-state]
  (let [etds (:etds app-state)
        data (u/all-minutes etds)]
    (-> (js/d3.select "g.timeline")
        (.selectAll "g.minute")
        (.data (clj->js data))
        .exit
        .remove
        )))
