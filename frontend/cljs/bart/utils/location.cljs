(ns bart.utils.location)

(defn showPosition [pos]
  (.log js.console pos))

(defn get-location [renderer]
  (.getCurrentPosition js/navigator.geolocation renderer))

