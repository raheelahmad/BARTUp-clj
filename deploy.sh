#!/bin/bash
echo "Cleaning"
lein clean
echo "Building CLJS for production"
lein cljsbuild once prod
echo "Committing app.js"
git add resources/public/js/app.js
git commit -m 'Compile JS'
echo "Pushing to origin"
git push origin
