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

var Config = {

}
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

module.exports = Config;
