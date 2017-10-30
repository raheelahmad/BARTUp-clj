(ns bart.views.viz.lifecycle
  (:require cljsjs.d3
            [bart.views.viz.utils :as u]))

(defn minutes-text [d]
  (let [minutes-str (.-minutes d)
        val (int minutes-str)
        minutes (cond
                  (neg? val) (- val)
                  (zero? val) "Leaving"
                  :else val)
        ]
    minutes))


(defn line-name-text [d]
  (let [destination-str (.-destination d)]
    destination-str))

(def x-center-offset 56)

(defn timeline-mount [app-state view-state]
  (let [etds (:etds app-state)
        station-name (get-in app-state [:station :name])
        y-scale (u/y-scale etds view-state)
        station-y (y-scale 0)
        station-name-x (+ x-center-offset 130)
        station-marker-g (-> (js/d3.select ".timeline")
                            (.append "g")
                            (.attr "class" "station-marker")
                            (.attr "transform" (str "translate(0, " station-y ")"
                                                )))
        station-marker-circle (-> station-marker-g
                           (.append "circle")
                           (.attr "r" 4)
                           (.attr "cx" x-center-offset)
                           )
        station-marker-line (-> station-marker-g
                                (.append "line")
                                (.attr "x1" (+ x-center-offset 10))
                                (.attr "x2" station-name-x)
                                )
        station-name-text (-> station-marker-g
                              (.append "text")
                              (.text station-name)
                              (.attr "x" (+ station-name-x 5))
                              (.attr "dy" 4)
                              )]))

(defn timeline-enter [app-state]
  (let [etds (:etds app-state)
        minutes (u/all-minutes etds)
        gs (-> (js/d3.select ".timeline")
               (.selectAll "g.minute")
               (.data (clj->js minutes))
               .enter
               (.append "g")
               (.attr "class" "minute"))
        circles (-> gs
                    (.append "circle")
                    (.attr "cx" x-center-offset)
                    (.attr "r" 3))
        minutes-text (-> gs
                 (.append "text")
                 (.attr "class" "minute")
                 (.attr "dx" (- x-center-offset 10))
                 (.attr "dy" 5))
        line-text (-> gs
                      (.append "text")
                      (.attr "class" "line-name")
                      (.attr "dx" (+ x-center-offset 10))
                      (.attr "dy" 5))
        ]))

(defn timeline-update [app-state view-state]
  (let [etds (:etds app-state)
        y-scale (u/y-scale etds view-state)
        data (u/all-minutes etds)
        gs (-> (js/d3.select ".timeline")
               (.selectAll "g.minute"))
        minute-texts (-> gs (.select "text.minute"))
        line-texts (-> gs (.select "text.line-name"))
        ]
    (-> gs
        (.transition (js/d3.transition))
        (.attr "transform"
                  (fn [d _]
                    (let [y (y-scale (.-minutes d))]
                      (str "translate(0, " y ")")))))
    (-> minute-texts
        (.text minutes-text))
    (-> line-texts
        (.text line-name-text)
        )
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
