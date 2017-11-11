(ns bart.views.suppl
  (:require [bart.data.db :as db]))

(defn header-comp
  []
  [:div {:class "hero top-header"}
    [:h2 "BART Estimated Time of Departures"]
    [:h4 "Choose a station near you or pick any one."
   ]]
  )

(defn footer []
  [:div {:class "footer"}
   [:span "by Raheel Ahmad. A quick "]
   [:a {:href "https://sakunlabs.com/blog/post/BARTup"} "blog post."]
   ])

(defn loading
  []
  [:div {:class "loader"}])

