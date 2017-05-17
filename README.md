
Forecastify
===========================

# Running locally from IDE

* Run `Main.scala`
* Ensure REST api is exposed at `localhost:9000`

# Building locally and running with docker
* Run
``` 
  sbt docker
```

# Features
* Implemented OpenWeatherMap API query for long-term forecast
* Implemented REST API for already fetched locations and their measurements
* Added basic React.js client to fetch data

# TODO
* Tests for API client and ForecastMonitoringActor