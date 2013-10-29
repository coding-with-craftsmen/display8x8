display8x8
==========

Sample code to drive a 8x8 dot-matrix display via i2c. 

This code sample shows how to drive the 8x8 dot-matrix display for which the technical details can be found at: http://www.adafruit.com/products/871

This example makes use of the Pi4J library to drive the display through i2c.

For wiring to the Pi see: 
http://learn.adafruit.com/matrix-7-segment-led-backpack-with-the-raspberry-pi/hooking-everything-up

To enable i2c on the Pi:
http://learn.adafruit.com/adafruits-raspberry-pi-lesson-4-gpio-setup/configuring-i2c

To draw circle's and square's, the basic drawPixel method is called from the various draw methods. The draw methods are taken from the Adafruit Arduino GFX libraries and converted to Java. Source: https://github.com/adafruit/Adafruit-GFX-Library

Todo: 
- add font 8x8 
- make character appear correctly (is now mirrored)
- make scroll text function
- ...