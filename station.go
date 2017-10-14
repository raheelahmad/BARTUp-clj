package main

import (
	"encoding/xml"
	"sort"
)

// Station The station
type Station struct {
	XMLName   xml.Name `xml:"station" json:"-"`
	Name      string   `xml:"name" json:"name"`
	Abbr      string   `xml:"abbr" json:"abbreviation"`
	Latitude  float64  `xml:"gtfs_latitude" json:"latitude"`
	Longitude float64  `xml:"gtfs_longitude" json:"longitude"`
	Address   string   `xml:"address" json:"address"`
	City      string   `xml:"city" json:"city"`
	County    string   `xml:"county" json:"county"`
}

// AllStations all the stations
type AllStations struct {
	Stations []Station `xml:"station" json:"stations"`
}

// AllStationsData the data
type AllStationsData struct {
	XMLName      xml.Name    `xml:"root" json:"-"`
	StationsRoot AllStations `xml:"stations" json:"root"`
}

// closestStation station closest to lat long
func closestStation(lat float64, long float64, stations []Station) Station {
	sort.Slice(stations, func(i, j int) bool {
		di := distance(lat, long, stations[i].Latitude, stations[i].Longitude)
		dj := distance(lat, long, stations[j].Latitude, stations[j].Longitude)
		return di < dj
	})
	return stations[0]
}
