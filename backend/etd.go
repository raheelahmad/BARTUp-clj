package main

import (
	"encoding/xml"

	"sort"
	"strconv"
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
	Name    string `xml:"name" json:"name"`
	Abbr    string `xml:"abbr" json:"abbr"`
	Address string `xml:"address" json:"address"`
	City    string `xml:"city" json:"city"`
	ETDs    []ETD  `xml:"etd" json:"etd"`
}

// AllETDsData the data
type AllETDsData struct {
	XMLName xml.Name       `xml:"root" json:"root"`
	ETDs    ETDStationInfo `xml:"station" json:"station"`
}

// --- Response ---

// StationJ for JSON
type StationJ struct {
	Name    string `json:"name"`
	Abbr    string `json:"abbr"`
	Address string `json:"address"`
	City    string `json:"city"`
}

// LineETDJ for JSON
type LineETDJ struct {
	Destination string   `json:"destination"`
	Direction   string   `json:"direction"`
	Color       string   `json:"color"`
	Minutes     []string `json:"minutes"`
}

// ByDirectionJ struct
type ByDirectionJ struct {
	Direction string     `json:"direction"`
	Lines     []LineETDJ `json:"lines"`
}

// ETDResponse JSON response wrapper for ETD
type ETDResponse struct {
	Station       StationJ       `json:"station"`
	DirectionETDs []ByDirectionJ `json:"etds"`
}

// NewETDResponse init for ETDResponse
func NewETDResponse(station Station, etdInfo ETDStationInfo) *ETDResponse {
	etds := []LineETDJ{}
	for _, etd := range etdInfo.ETDs {
		estimates := []string{}
		for _, estimate := range etd.Estimates {
			estimates = append(estimates, estimate.Minutes)
		}
		etds = append(etds, LineETDJ{Destination: etd.Destination,
			Direction: etd.Estimates[0].Direction,
			Color:     etd.Estimates[0].Hexcolor,
			Minutes:   estimates,
		})
	}
	sort.Slice(etds, func(i, j int) bool {
		if etds[i].Minutes[0] == "Leaving" {
			return true
		}
		if etds[j].Minutes[0] == "Leaving" {
			return false
		}
		iFirstMinuteStr, _ := strconv.ParseInt(etds[i].Minutes[0], 10, 32)
		jFirstMinuteStr, _ := strconv.ParseInt(etds[j].Minutes[0], 10, 32)
		return iFirstMinuteStr < jFirstMinuteStr
	})
	northLines := []LineETDJ{}
	southLines := []LineETDJ{}
	for _, etd := range etds {
		if etd.Direction == "North" {
			northLines = append(northLines, etd)
		} else {
			southLines = append(southLines, etd)
		}
	}

	return &ETDResponse{
		Station: StationJ{
			Name:    station.Name,
			Abbr:    station.Abbr,
			Address: station.Address,
			City:    station.City,
		},
		DirectionETDs: []ByDirectionJ{
			ByDirectionJ{Direction: "North", Lines: northLines},
			ByDirectionJ{Direction: "South", Lines: southLines},
		},
	}
}
