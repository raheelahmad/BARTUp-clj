package main

import (
	"fmt"
	"log"
	"strconv"

	"github.com/julienschmidt/httprouter"

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
	_, _ = w.Write(jsonBody)
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
	etdResponseForStation(station, w)
}

// StationETDHandler station ETD handler
func StationETDHandler(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	abbr := ps.ByName("station-abbr")
	station, err := stationForName(abbr)
	if err != nil {
		w.WriteHeader(http.StatusBadRequest)
		return
	}
	etdResponseForStation(*station, w)
}

func etdResponseForStation(station Station, w http.ResponseWriter) {
	etdInfo, err := GetETDs(station)
	if err != nil {
		w.WriteHeader(http.StatusBadRequest)
		return
	}
	jsonBody, _ := json.Marshal(etdInfo)
	w.Header().Set("Content-Type", "application/json")
	_, _ = w.Write(jsonBody)
}

func main() {
	router := httprouter.New()
	router.GET("/", Index)
	router.GET("/etd/:lat/:long", ETDHandler)
	router.GET("/station-etd/:station-abbr", StationETDHandler)
	router.GET("/stations", Stations)

	log.Fatal(http.ListenAndServe(":8080", router))
}
