(ns bart.views.viz.lifecycle
  (:require cljsjs.d3
            [reagent.core :as r]
            [bart.views.viz.utils :as u]))

(defonce fetched-at (r/atom 0))
(def x-center-offset 39)
(def left-text-type (r/atom :time))

(defn minutes-text [d]
  (let [minutes-str (.-minutes d)
        val (int minutes-str)
        minutes (cond
                  (neg? val) (- val)
                  (zero? val) "Leaving"
                  :else val)]
    minutes))

(defn time-text [d]
  (let [val (int (.-minutes d))
        minutes (if (neg? val) (- val) val)
        minutes-msecs (* 60000 minutes)
        offset-date (+ @fetched-at minutes-msecs)
        date (js/Date. offset-date)
        time (str (.getHours date) ":" (.getMinutes date))]
    time))

(defn update-arrival-times []
  (-> (js/d3.select ".timeline")
      (.selectAll "g.minute")
      (.select "text.minute")
      (.text (fn [d]
               (if (= @left-text-type :minutes)
                 (time-text d)
                 (minutes-text d)
                 )))))


(defn line-name-text [d]
  (let [destination-str (.-destination d)]
    destination-str))

(defn timeline-mount [app-state view-state]
  (let [station-name (get-in app-state [:station :name])
        y-scale (u/y-scale view-state)
        station-y (y-scale 0)
        station-name-x (+ x-center-offset 250)
        hover-rect (-> (js/d3.select ".timeline")
                       (.append "rect") (.attr "class" "hover-bg")
                       (.attr "width" (u/get-width view-state))
                       (.attr "height" (u/get-height view-state))
                       (.on "mousemove" (fn [d i]
                                          (this-as t
                                            (let [pt (js/d3.mouse t)
                                                  mouse-x (first pt)
                                                  new-type (if (> mouse-x x-center-offset)
                                                             :time :minutes)]
                                              (when-not (= new-type @left-text-type)
                                                (reset! left-text-type new-type)
                                                (update-arrival-times)
                                                ))))))
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
                              (.attr "x" station-name-x)
                              (.attr "dy" -4)
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
                    (.attr "r" 4)
                    (.attr "fill" (fn [d] (.-color d)))
                    )
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
        y-scale (u/y-scale view-state)
        data (u/all-minutes etds)
        gs (-> (js/d3.select ".timeline")
               (.selectAll "g.minute"))
        line-texts (-> gs (.select "text.line-name"))]
    (reset! fetched-at (:fetched-at app-state))
    (-> gs
        (.transition (js/d3.transition))
        (.attr "transform"
                  (fn [d _]
                    (let [y (y-scale (.-minutes d))]
                      (str "translate(0, " y ")")))))
    (update-arrival-times)
    (-> line-texts
        (.text line-name-text)
        )))

(defn timeline-exit [app-state]
  (let [etds (:etds app-state)
        data (u/all-minutes etds)]
    (-> (js/d3.select "g.timeline")
        (.selectAll "g.minute")
        (.data (clj->js data))
        .exit
        .remove
        )))
