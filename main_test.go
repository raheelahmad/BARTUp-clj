package main

import (
	"encoding/xml"
	"io/ioutil"
	"math"
	"testing"
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

func getXML() []byte {
	data, _ := ioutil.ReadFile("./etds.xml")
	return data
}

func checkFloat(v1 float64, v2 float64) bool {
	eps := 0.0001
	return math.Abs(v1-v2) <= eps
}
