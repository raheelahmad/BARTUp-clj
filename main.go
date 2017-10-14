package main

import (
	"fmt"
	"github.com/julienschmidt/httprouter"
	"log"
	"strconv"

	"encoding/json"
	"net/http"
)

// Index root handler
func Index(w http.ResponseWriter, r *http.Request, _ httprouter.Params) {
	fmt.Fprintf(w, "Welcome\n")
}

// Stations all stations
func Stations(w http.ResponseWriter, r *http.Request, _ httprouter.Params) {
	allStations, err := GetStations()
	if err != nil {
		fmt.Fprintln(w, "Sorry!")
	}
	jsonBody, _ := json.Marshal(allStations)
	w.Header().Set("Content-Type", "application/json")
	w.Write(jsonBody)
}

// ETDHandler gets ETDs for lat/long
func ETDHandler(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	stations, err0 := GetStations()
	lat, err1 := strconv.ParseFloat(ps.ByName("lat"), 64)
	long, err2 := strconv.ParseFloat(ps.ByName("long"), 64)
	if err0 != nil || err1 != nil || err2 != nil {
		w.WriteHeader(http.StatusBadRequest)
		return
	}
	station := closestStation(lat, long, stations)
	etdInfo, err := GetETDs(station)
	if err != nil {
		w.WriteHeader(http.StatusBadRequest)
		return
	}
	jsonBody, _ := json.Marshal(etdInfo)
	w.Header().Set("Content-Type", "application/json")
	w.Write(jsonBody)
}

func main() {
	router := httprouter.New()
	router.GET("/", Index)
	router.GET("/etd/:lat/:long", ETDHandler)
	router.GET("/stations", Stations)

	log.Fatal(http.ListenAndServe(":8080", router))
}
