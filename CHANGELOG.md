# 0.1.8

# Major features

* Broker will now require subscription requests to provide the complete URL for notifications.
  Previous behavior was to append the standard `/ngsi10/notifyContext` to the URL which is not done anymore.
  This allows applications to receive notifications with custom URL paths and parameters.

# Minor features

* Update to ngsi-api library 0.1.2

# Bug fixes

* print stacktrace on errors #56
* fix crash when parsing updateContext in JSON #57

# 0.1.7

# Minor features

* Update fiware-ngsi-api dependency to 0.1.1

*Warning:* Starting with this release, Cepheus Broker and CEP use JSON by default instead of XML for NGSI v1.
If a host initiates a NGSI exchange with Broker or CEP using XML, Cepheus components will keep communicating using XML.
XML support is now deprecated and will be removed in a future version of Fiware Cepheus.

# Bug fixes

* Fixes compatibility with Orion 1.0.0 and above #51 (by using JSON as default NGSI format)

# 0.1.6

## Major features

* move to fiware-ngsi-api library

## Minor features

* align badges for FIWARE Projects

## Bug fixes

# 0.1.5

## Major features

* REST NGSI10 support #39

## Minor features

* fix: add licenses headers #47

## Bug fixes

* broker: fix notifications with subset of attributes #46
* cep: clean subscription #44


# 0.1.4

## Major features

* Monitoring #35

## Minor features

* doc: generate the pdf of the documentation #37

## Bug fixes

* corrected coverity medium defects #38
* Detection of existing statements is not working #36

# 0.1.3

## Major features

* Support multi tenancy features #33

## Minor features

* fiware images #34
* jmeter integration #16, #19

## Bug fixes


# 0.1.2

## Major features

* Support geospatial features #18

## Minor features

* jmeter integration #16, #19

## Bug fixes

* CEP: support long type #28
* NGSI: fix error response format #27
* fix data persistence location for debian #22


# 0.1.1

## Major features

* Date and Geo:Point type support in Cepheus-CEP #14
* Filter forward updateContext requests in Cepheus-CEP #13
* Registration persistence in Cepheus-Broker #8
* Subscription persistence in Cepheus-Broker #6
* Linksprite demo application #7
* Jsonpath support in Cepheus-CEP #5

## Minor features

* Updated documentation

## Bug fixes

* Add support for updateContextSubscription request #12
* Fix XMl serialization #11 #9
* QueryContext sends empty restriction #4

# 0.1.0

## Major features

* Split project in two separated components (Cepheus-Broker and Cepheus-CEP)
* Support for NGSI v1 with JSON format (for compatibility with [IoT Agent](https://github.com/telefonicaid/iotagent-node-lib))
* Support Orion `Fiware-Service` and `Fiware-ServicePath` custom HTTP headers

## Minor features

* Rename project to Fiware-Cepheus (old name: EspeR4FastData)
* Unit tested code and continuous integration with [Travis-ci](https://travis-ci.org/Orange-OpenSource/fiware-cepheus)
* Debian packaging for Debian/Raspbian
* Docker packaging on [Docker Hub](https://hub.docker.com/r/orangeopensource/fiware-cepheus/)
* Documenation in markdown format hosted on [ReadTheDocs](https://fiware-cepheus.readthedocs.org/en/latest/)

## Bug fixes

None

