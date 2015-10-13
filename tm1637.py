import sys
import os
import time
import RPi.GPIO as IO
import zerorpc

IO.setwarnings(False)
IO.setmode(IO.BCM)

HexDigits = [0x3f,0x06,0x5b,0x4f,0x66,0x6d,0x7d,0x07,0x7f,0x6f,0x77,0x7c,0x39,0x5e,0x63,0x00]

ADDR_AUTO = 0x40
ADDR_FIXED = 0x44
STARTADDR = 0xC0
BRIGHT_DARKEST = 0
BRIGHT_TYPICAL = 2
BRIGHT_HIGHEST = 7
OUTPUT = IO.OUT
INPUT = IO.IN
LOW = IO.LOW
HIGH = IO.HIGH

class TM1637:
	__doublePoint = False
	__Clkpin = 0
	__Datapin = 0
	__brightness = BRIGHT_TYPICAL;
	__currentData = [0,0,0,0];

	def __init__( self, pinClock, pinData, brightness ):
		self.__Clkpin = pinClock
		self.__Datapin = pinData
		self.__brightness = brightness;
		IO.setup(self.__Clkpin,OUTPUT)
		IO.setup(self.__Datapin,OUTPUT)
	# end  __init__

	def Clear(self):
		b = self.__brightness;
		point = self.__doublePoint;
		self.__brightness = 0;
		self.__doublePoint = False;
		data = [0x7F,0x7F,0x7F,0x7F];
		self.Show(data);
		self.__brightness = b;				# restore saved brightness
		self.__doublePoint = point;
	# end  Clear

	def Show( self, data ):
		for i in range(0,4):
			self.__currentData[i] = data[i];

		self.start();
		self.writeByte(ADDR_AUTO);
		self.stop();
		self.start();
		self.writeByte(STARTADDR);
		for i in range(0,4):
			self.writeByte(self.coding(data[i]));
		self.stop();
		self.start();
		self.writeByte(0x88 + self.__brightness);
		self.stop();
	# end  Show

	def Show1(self, DigitNumber, data):	# show one Digit (number 0...3)
		if( DigitNumber < 0 or DigitNumber > 3):
			return;	# error

		self.__currentData[DigitNumber] = data;

		self.start();
		self.writeByte(ADDR_FIXED);
		self.stop();
		self.start();
		self.writeByte(STARTADDR | DigitNumber);
		self.writeByte(self.coding(data));
		self.stop();
		self.start();
		self.writeByte(0x88 + self.__brightness);
		self.stop();
	# end  Show1

	def SetBrightness(self, brightness):		# brightness 0...7
		if( brightness > 7 ):
			brightness = 7;
		elif( brightness < 0 ):
			brightness = 0;

		if( self.__brightness != brightness):
			self.__brightness = brightness;
			self.Show(self.__currentData);
		# end if
	# end  SetBrightness

	def ShowDoublepoint(self, on):			# shows or hides the doublepoint
		if( self.__doublePoint != on):
			self.__doublePoint = on;
			self.Show(self.__currentData);
		# end if
	# end  ShowDoublepoint

	def writeByte( self, data ):
		for i in range(0,8):
			IO.output( self.__Clkpin, LOW)
			if(data & 0x01):
				IO.output( self.__Datapin, HIGH)
			else:
				IO.output( self.__Datapin, LOW)
			data = data >> 1
			IO.output( self.__Clkpin, HIGH)
		#endfor

		IO.output( self.__Clkpin, LOW)
		IO.output( self.__Datapin, HIGH)
		IO.output( self.__Clkpin, HIGH)
		IO.setup(self.__Datapin, INPUT)

		while(IO.input(self.__Datapin)):
			time.sleep(0.001)
			if( IO.input(self.__Datapin)):
				IO.setup(self.__Datapin, OUTPUT)
				IO.output( self.__Datapin, LOW)
				IO.setup(self.__Datapin, INPUT)
			#endif
		# endwhile
		IO.setup(self.__Datapin, OUTPUT)
	# end writeByte

	def start(self):
		IO.output( self.__Clkpin, HIGH) # send start signal to TM1637
		IO.output( self.__Datapin, HIGH)
		IO.output( self.__Datapin, LOW)
		IO.output( self.__Clkpin, LOW)
	# end start

	def stop(self):
		IO.output( self.__Clkpin, LOW)
		IO.output( self.__Datapin, LOW)
		IO.output( self.__Clkpin, HIGH)
		IO.output( self.__Datapin, HIGH)
	# end stop

	def coding(self, data):
		if( self.__doublePoint ):
			pointData = 0x80
		else:
			pointData = 0;

		if(data == 0x7F):
			data = 0
		else:
			data = HexDigits[data] + pointData;
		return data
	# end coding

# end class TM1637

Display = TM1637(sys.argv[1],sys.argv[2],BRIGHT_TYPICAL)

Display.Clear()

class HelloRPC(object):
    def displayTemp(self, t):
        temp = float(t);
        firstDigit = int(temp / 10)
        secondDigit = int(temp - (firstDigit * 10))
        Display.ShowDoublepoint(False)
        Display.Show1(0, firstDigit)
        Display.Show1(1, secondDigit)
        Display.Show1(2, 14)
        Display.Show1(3, 15)

s = zerorpc.Server(HelloRPC())
s.bind("tcp://0.0.0.0:4242")
s.run()
