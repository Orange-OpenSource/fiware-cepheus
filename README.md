
## **Sprite NGSI** ##

LinkSprite-NGSI is an application for a [RaspberryPi](https://www.raspberrypi.org) with [LinkerKit](http://store.linksprite.com/raspberry-pi/) components. LinkSprite-NGSI is like a [NGSI](http://technical.openmobilealliance.org/Technical/technical-information/release-program/current-releases/ngsi-v1-0) component which send [NGSI](http://technical.openmobilealliance.org/Technical/technical-information/release-program/current-releases/ngsi-v1-0) events (temperature, light) and can execute a command to turn on/off the LED ([Command Description](https://github.com/telefonicaid/iotagent-node-lib#commands)).
This application uses the [Fiware IoT Agent Framework](https://github.com/telefonicaid/iotagent-node-lib) and works with [Fiware Cepheus](https://github.com/Orange-OpenSource/fiware-cepheus).

List of LinkSprite hardware used for the application :
 - [LinkerKit Base Shield](http://linksprite.com/wiki/index.php5?title=Linker_kit_Base_Shield_for_Raspberry_Pi_with_ADC_Interface)
 - [LinkerKit LED](http://linksprite.com/wiki/index.php5?title=3mm_Green_LED_Module)
 - [LinkerKit Temperature Sensor](http://linksprite.com/wiki/index.php5?title=Thermal_Module)
 - [LinkerKit LightSensor](http://linksprite.com/wiki/index.php5?title=LDR_Module)
 - [LinkerKit 7 segments](http://linksprite.com/wiki/index.php5?title=4-Digit_7-Segment_Module)

The application is configurable by editing the config.js file :

    // Red Led
	Config.IS_PLUGGED_REDLED = true;
	Config.GPIO_PORT_REDLED = 13;
	// Blue Led
	Config.IS_PLUGGED_BLUELED = true;
	Config.GPIO_PORT_BLUELED = 11;
	// 7Segments Display
	Config.IS_PLUGGED_7SEGMENTS = true;
	Config.GPIO_PORT1_7SEGMENTS = 23;
	Config.GPIO_PORT2_7SEGMENTS = 24;
	// Temperature / ADC
	Config.IS_PLUGGED_TEMPERATURE = true;
	Config.ADC_CHANNEL_TEMPERATURE = 2;
	// Light Sensor / ADC
	Config.IS_PLUGGED_LIGHTSENSOR = true
	Config.ADC_CHANNEL_LIGHTSENSOR = 0;

	Config.LOG_LEVEL = 'DEBUG';
	Config.CONTEXT_BROKER_HOST = 'localhost';
	Config.CONTEXT_BROKER_PORT = '8081';
	Config.MY_IP_ADDRESS = 'Fiware1';
	Config.MY_PORT = '4041';

You can use this application to implement different use case with Cepheus.
The following Cepheus CEP configuration file will send command to LinkSprite-NGSI to turn on/off the LED when the temperature and light sensor is changing for a certain time.

    {
	  "host" : "http://cepheus:8080",
	  "in": [
	    {
	      "id": "temperature*",
	      "type": "Temperature",
	      "isPattern": true,
	      "attributes": [
	        {
	          "name": "temperatureValue",
	          "type": "float"
	        }
	      ],
	      "providers": ["http://cepheus:8081"]
	    },
	    {
	      "id": "light*",
	      "type": "Light",
	      "isPattern": true,
	      "attributes": [
	        {
	          "name": "lightValue",
	          "type": "float"
	        }
	      ],
	      "providers": ["http://cepheus:8081"]
	    }
	  ],
	  "out": [
		    {
		      "id": "redLed",
		      "isPattern":false,
		      "type": "Led",
		      "attributes": [
		        {
		          "name": "isOn",
		          "type": "string"
		        }
		      ],
		      "brokers": [
		        {
		          "url": "http://cepheus:8081"
		        }
		      ]
		    },
	    {
	      "id": "blueLed",
	      "isPattern":false,
	      "type": "Led",
	      "attributes": [
	        {
	          "name": "isOn",
	          "type": "string"
	        }
	      ],
	      "brokers": [
	        {
	          "url": "http://cepheus:8081"
	        }
	      ]
	    }
	  ],
	  "statements": [
		    "INSERT INTO Led SELECT 'redLed' as id, 'true' as isOn FROM pattern [ every t=Temperature(temperatureValue > 25) -> (timer:interval(10 sec) and not Temperature(temperatureValue < 25 and id=t.id))]",
    "INSERT INTO Led SELECT 'redLed' as id, 'false' as isOn FROM pattern [ every t=Temperature(temperatureValue <= 25) -> (timer:interval(10 sec) and not Temperature(temperatureValue > 25 and id=t.id))]",
    "INSERT INTO Led SELECT 'blueLed' as id, 'true' as isOn FROM pattern [ every t=Light(lightValue > 50) -> (timer:interval(10 sec) and not Light(lightValue < 50 and id=t.id))]",
    "INSERT INTO Led SELECT 'blueLed' as id, 'false' as isOn FROM pattern [ every t=Light(lightValue < 50) -> (timer:interval(10 sec) and not Light(lightValue > 50 and id=t.id))]"]
    }
