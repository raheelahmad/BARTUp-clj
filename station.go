package main

import (
	"encoding/xml"
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
