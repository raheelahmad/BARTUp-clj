#!/bin/bash
lein clean
lein cljsbuild once prod
git add resources/public/js/app.js
git commit -m 'Compile JS'
git push origin
