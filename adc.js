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

var spi = require('pi-spi');
spi.initialize('/dev/spidev0.0');

exports.read = function(channel, callback) {
    if (spi === undefined)
    {
      console.log('!> spi is not defined.');
      return;
    }
    var mode = (8 + channel) << 4;

    var tx = new Buffer([1, mode, 0]);
    var rx = new Buffer([0, 0, 0]);

    spi.transfer(tx, tx.length, function(dev, buffer) {
        var value = ((buffer[1] & 3) << 8) + buffer[2];
        callback(value);
    })
}
