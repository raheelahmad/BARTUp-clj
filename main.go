package main

import (
	"fmt"
	"github.com/julienschmidt/httprouter"
	"log"

	"net/http"
)

// Index root handler
func Index(w http.ResponseWriter, r *http.Request, _ httprouter.Params) {
	fmt.Fprintf(w, "Welcome\n")
}

// ETDHandler gets ETDs for lat/long
func ETDHandler(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	fmt.Fprintf(w, "Get for lat: %s, long: %s", ps.ByName("lat"), ps.ByName("long"))
}

func main() {
	router := httprouter.New()
	router.GET("/", Index)
	router.GET("/etd/:lat/:long", ETDHandler)

	log.Fatal(http.ListenAndServe(":8080", router))
}
