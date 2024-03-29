(ns bart.views.viz.lifecycle
  (:require cljsjs.d3
            [reagent.core :as r]
            [bart.views.viz.utils :as u]
            [bart.data.db :as db]))

(defonce fetched-at (r/atom 0))
(def x-center-offset 58)
(def left-text-type (r/atom :time))

(defn minutes-text [d]
  (let [minutes-str (aget d "minutes")
        val (int minutes-str)
        minutes (cond
                  (neg? val) (- val)
                  (zero? val) "Leaving"
                  :else val)]
    minutes))

(defn time-text [d]
  (let [val (int (aget d "minutes"))
        minutes (if (neg? val) (- val) val)
        minutes-msecs (* 60000 minutes)
        offset-date (+ @fetched-at minutes-msecs)
        date (js/Date. offset-date)
        time (str (.getHours date) ":" (.getMinutes date))]
    time))

(defn line-name-text [d]
  (let [destination-str (aget d "destination")]
    destination-str))

(defn update-arrival-times
  "Updates the time text (time or mintues) based on assigned datum"
  []
  (-> (js/d3.select ".timeline")
      (.selectAll "g.minute")
      (.select "text.minute")
      (.text (fn [d]
               (if (= @left-text-type :minutes)
                 (time-text d)
                 (minutes-text d)
                 )))))

(defn update-hovered-line
  "Updates hovered lines"
  []
  (-> (js/d3.select ".timeline")
      (.selectAll "g.minute")
      (.select "text.line-name")
      (.attr "class" (fn [d]
               (let [line-name (aget d "destination")
                     is-hovered (= line-name @db/hovering-line)]
                 (if is-hovered "line-name highlighted-line" "line-name")
                 )))))

(defn hovered [t y-scale]
  (let [pt (js/d3.mouse t)
        mouse-x (first pt)
        mouse-y (second pt)
        right-of-timeline (> mouse-x x-center-offset)
        new-type (if right-of-timeline :time :minutes)
        hovered-minute (.invert y-scale mouse-y)
        hovered-over-line (->> (js/d3.selectAll "g.minute")
                               (.data)
                               (filter (fn [d]
                                         (let [minutes (aget d "minutes")
                                               distance (js/Math.abs (- minutes hovered-minute))]
                                           (< distance 2))))
                               first
                               )]
    ;; Toggle arrival time b/w mintues / time if needed
    (when-not (= new-type @left-text-type)
      (reset! left-text-type new-type)
      (update-arrival-times))
    ;; Set hovered-line value if hovering over one
    (reset! db/hovering-line (if (nil? hovered-over-line) nil
                               (aget hovered-over-line "destination")))
    ;; update visual style for hovered
    (update-hovered-line)
    ))

(defn timeline-mount [app-state view-state]
  (let [station-name (get-in app-state [:station :name])
        station-address (str (get-in app-state [:station :address])
                             ", "
                             (get-in app-state [:station :city])
                             )
        y-scale (u/y-scale app-state view-state)
        station-y (y-scale 0)
        station-name-x (- (u/get-width view-state) 30)

        direction-bound-texts (let [bound-text (fn [text going-up?]
                                                 (let
                                                     [x (if going-up?
                                                          station-name-x
                                                          (- station-name-x 11))
                                                      y (+ (if going-up? 30 -30) station-y)
                                                      angle (if going-up? (- 90) 90)]
                                                   (-> (js/d3.select ".timeline")
                                                       (.append "text") (.attr "class" "timeline-direction")
                                                       (.attr "transform"
                                                              (str "translate(" x "," y ")rotate(" angle ")"))
                                                     (.text (str text " bound →")))))]
                                (bound-text "North" :yes)
                                (bound-text "South" nil))

        ;; these 4 are for the static station in the middle
        station-marker-g (-> (js/d3.select ".timeline")
                             (.append "g")
                             (.attr "class" "station-marker")
                             (.attr "transform" (str "translate(0, " station-y ")"
                                                     )))
        station-marker-line (-> station-marker-g
                                (.append "line")
                                (.attr "x1" (+ x-center-offset 3))
                                (.attr "x2" station-name-x))

        station-name-text (-> station-marker-g
                              (.append "text") (.attr "class" "name")
                              (.text station-name)
                              (.attr "x" station-name-x)
                              (.attr "dy" -4))
        station-address-text (-> station-marker-g
                                 (.append "text") (.attr "class" "address")
                                 (.attr "x" station-name-x) (.attr "dy" 12)
                                 (.text station-address)
                                 )

        ;; To track mouse movement
        hover-rect (-> (js/d3.select ".timeline")
                       (.append "rect") (.attr "class" "hover-bg")
                       (.attr "width" (u/get-width view-state))
                       (.attr "height" (u/get-height view-state))
                       (.on "mousemove" (fn [d i]
                                          (this-as t
                                            (hovered t y-scale)))))

        _ (add-watch db/hovering-line
                     :watch-for-hover-from-listings-side
                     (fn [_ _ _ _]
                       (update-hovered-line)))

        ]))

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
                    (.attr "fill" (fn [d] (aget d "color")))
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
        gs (-> (js/d3.select ".timeline")
               (.selectAll "g.minute"))
        line-texts (-> gs (.select "text.line-name"))
        y-scale (u/y-scale app-state view-state)]
    (reset! fetched-at (:fetched-at app-state))
    (-> gs
        (.transition (js/d3.transition))
        (.attr "transform"
                  (fn [d i]
                    (let [
                          y (y-scale (aget d "minutes"))
                          ]
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
