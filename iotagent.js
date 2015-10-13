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

  'use strict';

  var async = require('async');
  var iotAgent = require('iotagent-node-lib');
  var http = require('http');
  var express = require('express');
  var config = require('./config.js')
  var expressApp = express();
  var app = http.createServer(expressApp);

  var configServer = {
    ip : '127.0.0.1',
    port : '4049'
  }

  var iotAgentConfig = {
    logLevel: config.LOG_LEVEL,
    contextBroker: {
        host: config.CONTEXT_BROKER_HOST,
        port: config.CONTEXT_BROKER_PORT
    },
    server: {
        port: 4041
    },
    providerUrl: 'http://' + config.MY_IP_ADDRESS + ':' + config.MY_PORT,
    deviceRegistrationDuration: 'P1M',
    types: {
        'Temperature': {
            url: '/',
            apikey: '',
            type: 'Temperature',
            // service: '',
            // subservice: '',
            // trust: ''
            // cbHost: '',
            commands: [],
            lazy: [],
            active: [
                {
                    name: 'temperatureValue',
                    type: 'float'
                }
            ]
        },
        'Light': {
            url: '/',
            apikey: '',
            type: 'Light',
            // service: '',
            // subservice: '',
            // trust: ''
            // cbHost: '',
            commands: [],
            lazy: [],
            active: [
                {
                    name: 'lightValue',
                    type: 'float'
                }
            ]
        },
        'Led': {
            commands: [
                {
                    name: 'isOn',
                    type: 'string'
                }
            ],
            lazy: [],
            staticAttributes: [],
            active: []
        }
    }
  }

  expressApp.configure(function() {
      expressApp.use(express.errorHandler({ dumpExceptions: true, showStack: true }));
      expressApp.use(express.bodyParser());
      expressApp.use(express.static(__dirname + '/web'));
      expressApp.enable('trust proxy');
      expressApp.use(function(req, res, next) {
        console.log('!> %s %s %s %s', req.protocol, req.ip, req.method, req.url);
      });
  });

  function updateConfigurationHandler(newConfiguration, callback) {
      console.log('!> Unsupported configuration update received');
      callback();
  }

  function init(callback)
  {
    app.listen(configServer.port, configServer.ipaddress);
    iotAgent.activate(iotAgentConfig, function(err, res)
    {
      if (err)
        callback(err, null);
      iotAgent.setConfigurationHandler(updateConfigurationHandler);
      console.log('!> IoT Agent Fiware Launched');
      console.log('!> IoT Agent, Listening on ip :' + configServer.ip + ' and port : ' + configServer.port);
      callback(null, iotAgent);
    });
  }

  exports.init = init;
