package main

import (
	"encoding/xml"
)

// Estimate the estimate
type Estimate struct {
	Minutes   string `xml:"minutes" json:"minutes"`
	Platform  int    `xml:"platform" json:"platform"`
	Direction string `xml:"direction" json:"direction"`
	Color     string `xml:"color" json:"color"`
	Hexcolor  string `xml:"hexcolor" json:"hexcolor"`
	Delay     int    `xml:"delay" json:"delay"`
}

// ETD ETD info
type ETD struct {
	Destination  string     `xml:"destination" json:"destination"`
	Abbreviation string     `xml:"abbreviation" json:"abbreviation"`
	Estimates    []Estimate `xml:"estimate" json:"estimate"`
}

// ETDStationInfo station info
type ETDStationInfo struct {
	Name string `xml:"name" json:"name"`
	Abbr string `xml:"abbr" json:"abbr"`
	ETDs []ETD  `xml:"etd" json:"etd"`
}

// AllETDsData the data
type AllETDsData struct {
	XMLName xml.Name       `xml:"root" json:"root"`
	ETDs    ETDStationInfo `xml:"station" json:"station"`
}
