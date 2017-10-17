package main

import (
	"encoding/xml"
	"fmt"
	"io/ioutil"
	"net/http"
)

// StationETDURL URL for ETD
func StationETDURL(station Station) string {
	URLFormat := "https://api.bart.gov/api/etd.aspx?cmd=etd&orig=%s&key=RLK2-XZYL-QHJQ-4YR8"
	return fmt.Sprintf(URLFormat, station.Abbr)
}

// GetETDs get ETDs for the station
func GetETDs(station Station) (*ETDResponse, error) {
	URL := StationETDURL(station)

	resp, err := http.Get(URL)
	if err != nil {
		return nil, err
	}
	bodyBytes, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return nil, err
	}

	var data AllETDsData
	err = xml.Unmarshal(bodyBytes, &data)
	if err != nil {
		return nil, err
	}
	return NewETDResponse(data.ETDs), nil
}

// GetStations Get stations
func GetStations() ([]Station, error) {
	bytes, err := ioutil.ReadFile("stations.dat")
	if err != nil || len(bytes) == 0 {
		fmt.Println("No stations XML file on disk; fetching...")
		resp, err := http.Get("https://api.bart.gov/api/stn.aspx?cmd=stns&key=RLK2-XZYL-QHJQ-4YR8")
		if err != nil {
			return nil, err
		}
		bytes, err = ioutil.ReadAll(resp.Body)
		err = ioutil.WriteFile("stations.dat", bytes, 0644)
		if err != nil {
			fmt.Println("Error writing to stations: ", err)
		}
	}

	var data AllStationsData
	err = xml.Unmarshal(bytes, &data)
	if err != nil {
		return nil, err
	}

	return data.StationsRoot.Stations, nil
}
