/*
Copyright (C) 2015  Orange

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

var http = require('http');
var sleep = require('sleep');
var led = require('./led.js');
var adc = require('./adc.js');
var iot = require('./iotagent.js');
var config = require('./config.js');
var zerorpc = require('zerorpc');
var client = new zerorpc.Client();
var python = require('child_process').spawn(
     'python',
     // second argument is array of parameters, e.g.:
     ["./tm1637.py"],
     config.GPIO_PORT1_7SEGMENTS,
     config.GPIO_PORT2_7SEGMENTS
);
var CronJob = require('cron').CronJob;
var iotagentLib = require('iotagent-node-lib');

console.log('!>\n!> Start\n');
console.log('!> waiting for start');
sleep.sleep(1);
console.log('!> connect to 7-segments display');
client.connect("tcp://127.0.0.1:4242");
console.log('!> init led');
led.initLed(function(err)
{
  if (err)
    throw err;
});

process.on('SIGTERM', function () {
  server.close(function () {
    led.closeLed();
    console.log('CLOSE');
    process.exit(0);
  });
});

console.log('!> Init IoT Agent');
iot.init(function(err, iotAgent)
{
  if (err)
    throw err;
  // Register Command
  redLed = {
      id: 'redLed',
      name:'redLed',
      type: 'Led'
  };
  blueLed = {
      id: 'blueLed',
      name:'blueLed',
      type: 'Led'
  };
  iotAgent.register(redLed, function(err, result) {});
  iotAgent.register(blueLed, function(err, result) {});

  // update handler
  iotAgent.setDataUpdateHandler(function(id, type, attributes, callback) {
      console.log("!> update handler!")
      callback(null, {
        type: type,
        isPattern: false,
        id: id,
        attributes: []
      });
  });

  // set command handler
  iotAgent.setCommandHandler(function(id, type, attributes, callback) {
      console.log('!> command handler!')
      if (id == redLed.id)
      {
        led.lightRed(attributes[0].value);
      }
      else if (id == blueLed.id)
      {
        led.lightBlue(attributes[0].value);
      }
      callback(null, {
          id: id,
          type: type,
          attributes: attributes
      });
  });
  console.log('!> init cron job');
  new CronJob('*/2 * * * * *', function() {
    getTemperature(function(err, value) {
      if (err)
        console.log('!> Error when reading temperature value');

      iotAgent.update('temperatureRPi1', 'Temperature', '', [ { "name": "temperatureValue", "type":"float", "value":value } ], function(err, result)
      {
        if (err)
          console.log(err);
      });
    });
    getLightValue(function(err, value) {
      if (err)
        console.log('!> Error when reading Light sensor');

      iotAgent.update('lightRPi1', 'Light', '', [ { "name": "lightValue", "type":"float", "value":value } ], function(err, result)
        {
          if (err)
            console.log(err);
        });
    });
  }, null, true, "America/Los_Angeles");
});

function getTemperature(callback)
{
  adc.read(config.ADC_CHANNEL_TEMPERATURE, function(value) {
    var temperature = temperatureForValue(value);
    console.log('!> Temperature : ' + temperature + '°');
    //calls the method on the python object
    client.invoke("displayTemp", temperature, function(error, reply, streaming) {
        if(error){
            console.log("!> error on display temp: ", error);
        }
    });
    callback(null, temperature);
  });
}

function getLightValue(callback)
{
  adc.read(config.ADC_CHANNEL_LIGHTSENSOR, function(value) {
    var v = (1023-value)*10/value;
    console.log('!> Light Level : ' + v);
    callback(null, v);
  });
}

function temperatureForValue(value) {
    var volts = (value * 3.3) / 1024;
    var temp = (volts - 0.5) * 100;
    return temp;
}
