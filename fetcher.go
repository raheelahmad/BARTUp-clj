package main

import (
	"encoding/json"
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
func GetETDs(station Station) (*ETDStationInfo, error) {
	URL := StationETDURL(station)

	resp, err := http.Get(URL)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()
	bodyBytes, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return nil, err
	}

	var data AllETDsData
	err = xml.Unmarshal(bodyBytes, &data)
	if err != nil {
		return nil, err
	}
	jsonBody, _ := json.Marshal(data)
	fmt.Println(string(jsonBody))
	return &data.ETDs, nil
}

// GetStations Get stations
func GetStations() ([]Station, error) {
	resp, err := http.Get("https://api.bart.gov/api/stn.aspx?cmd=stns&key=RLK2-XZYL-QHJQ-4YR8")
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()
	bodyBytes, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return nil, err
	}

	var data AllStationsData
	err = xml.Unmarshal(bodyBytes, &data)
	if err != nil {
		return nil, err
	}
	return data.StationsRoot.Stations, nil
	// jsonBody, _ := json.Marshal(data)
	// fmt.Println(string(jsonBody))
}
