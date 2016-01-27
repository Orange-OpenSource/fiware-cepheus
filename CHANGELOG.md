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

