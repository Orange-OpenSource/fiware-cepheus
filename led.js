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

var config = require('./config.js');
var gpio = require("pi-gpio");

exports.initLed = function(callback)
{
  this.closeLed();
  if (config.IS_PLUGGED_REDLED)
  {
    gpio.open(config.GPIO_PORT_REDLED, 'output', function(err)
    {
      if (err)
        callback(err);
    });
  }
  if (config.IS_PLUGGED_BLUELED)
  {
    gpio.open(config.GPIO_PORT_BLUELED, 'output', function(err)
    {
      if (err)
        callback(err);
      });
  }
}

exports.closeLed = function()
{
  if (config.IS_PLUGGED_REDLED)
  {
    gpio.close(config.GPIO_PORT_REDLED);
  }
  if (config.IS_PLUGGED_BLUELED)
  {
    gpio.close(config.GPIO_PORT_BLUELED);
  }
}

exports.lightRed = function(on)
{
  if (config.IS_PLUGGED_REDLED)
  {
    if (on == 'true')
    {
      gpio.write(config.GPIO_PORT_REDLED, 1);
    }
    else {
      gpio.write(config.GPIO_PORT_REDLED, 0);
    }
  }
}

exports.lightBlue = function(on)
{
  if (config.IS_PLUGGED_BLUELED)
  {
    if (on == 'true')
    {
      gpio.write(config.GPIO_PORT_BLUELED, 1);
    }
    else {
      gpio.write(config.GPIO_PORT_BLUELED, 0);
    }
  }
}
