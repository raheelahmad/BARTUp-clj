package main

import (
	"encoding/xml"
	"io/ioutil"
	"math"
	"testing"

	. "github.com/franela/goblin"
)

func TestDistance(t *testing.T) {
	dist := distance(38.898556, -77.037852, 38.897147, -77.043934)
	if !checkFloat(dist, 0.549768) {
		t.Errorf("Incorrect distance %f", dist)
	}
}

func TestETDs(t *testing.T) {
	xmlBytes := getXML()
	var data AllETDsData
	err := xml.Unmarshal(xmlBytes, &data)
	if err != nil {
		t.Error("Could not unmarshal")
	}
}

func TestStationForName(t *testing.T) {
	station, err := stationForName("UCTY")
	if err != nil || station == nil || station.Abbr != "UCTY" {
		t.Error("Could not fetch station by name")
	}
}

func TestETDResponse(t *testing.T) {
	etdWSP := ETD{Destination: "Warm Springs", Abbreviation: "WSP", Estimates: []Estimate{
		Estimate{Minutes: "23", Direction: "South"},
		Estimate{Minutes: "31", Direction: "South"},
	}}
	etdDLY := ETD{Destination: "Daly City", Abbreviation: "DLY", Estimates: []Estimate{
		Estimate{Minutes: "11", Direction: "North"},
		Estimate{Minutes: "20", Direction: "North"},
	}}
	etdRCHMND := ETD{Destination: "Richmond", Abbreviation: "RCHMND", Estimates: []Estimate{
		Estimate{Minutes: "Leaving", Direction: "North"},
		Estimate{Minutes: "10", Direction: "North"},
	}}
	etds := []ETD{etdWSP, etdDLY, etdRCHMND}
	etdInfo := ETDStationInfo{Name: "Union City", Abbr: "UCTY", ETDs: etds}
	etdResponse := NewETDResponse(etdInfo)

	g := Goblin(t)
	g.Describe("ETD Response", func() {
		g.Describe("Station Info", func() {
			g.It("should have correct station name", func() {
				g.Assert(etdResponse.Station.Name).Equal("Union City")
			})
			g.It("should have correct station abbreviation", func() {
				g.Assert(etdResponse.Station.Abbr).Equal("UCTY")
			})
		})
		g.Describe("ETDs Info", func() {
			g.It("should have correct number of directions", func() {
				g.Assert(len(etdResponse.DirectionETDs)).Equal(2)
			})
			g.It("should order by earliest ETD", func() {
				northLines := etdResponse.DirectionETDs[0]
				southLines := etdResponse.DirectionETDs[1]
				firstETD := northLines.Lines[0]
				secondETD := northLines.Lines[1]
				thirdETD := southLines.Lines[0]
				g.Assert(firstETD.Destination).Equal("Richmond")
				g.Assert(secondETD.Destination).Equal("Daly City")
				g.Assert(thirdETD.Destination).Equal("Warm Springs")
			})
			g.It("should have correct estimates", func() {
				firstETD := etdResponse.DirectionETDs[0].Lines[0]
				g.Assert(firstETD.Minutes[0]).Equal("Leaving")
				g.Assert(firstETD.Minutes[1]).Equal("10")
			})
		})
	})
}

func getXML() []byte {
	data, _ := ioutil.ReadFile("./etds.xml")
	return data
}

func checkFloat(v1 float64, v2 float64) bool {
	eps := 0.0001
	return math.Abs(v1-v2) <= eps
}
