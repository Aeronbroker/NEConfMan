NEConfMan Docker file
=======================

NEConfMan is the NEC implementation of the FIWARE IoT Discovery GE.

This dockerfile can be used for building a docker image running the NEConfMan component. The build process will install the latest version available on GitHub (https://github.com/Aeronbroker/NEConfMan).

Running the image (you may need root permissions) will start a docker container with the NEConfMan running and listening to port 8065 and the log of the NEConfMan will be shown:

How to build?
=======================
Run 
```
docker build -t nle/neconfman:dev .
```


How to run?
=======================
In order to run the NEConfMan please do the following:

```
docker run -p 8065:8065 nle/neconfman:dev
```

If you want to run the IoTBroker docker container in background use the following:
```
docker run -p 8065:8065 nle/neconfman:dev > /dev/null &
```

Possibility to access to the CouchDB server
```
docker run -t -p 8065:8065 -p 5987:5984 nle/neconfman:dev
```





