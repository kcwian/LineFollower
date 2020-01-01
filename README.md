# LineFollower

## General Information
Package consists of two folders:
- Android: Android app used for configuration and manual control of robot. App was build using Eclipse with Android Plug-in.
- AVR: C code for Atmel AVR microcontroller (Atmega8). Code can be build using Eclipse with AVR tools or Linux terminal as shown below.
## Compiling code for AVR microcontroller (Atmega8) using Linux
- Installing necessary tools:
```
$ sudo apt-get install gcc-avr avr-libc avrdude
```
- Generating *.bin file:
```
$ cd AVR
$ avr-gcc -Wall -g -Os -mmcu=atmega8 -o main.bin linefollower.c functions.c
```
- Generating *.hex file:
```
$ avr-objcopy -j .text -j .data -O ihex main.bin main.hex
```

- Flashing using USBasp programmer:
```
$ avrdude -p atmega8 -c usbasp -U flash:w:main.hex:i -F -P usb
```

## Android App

- Main screen:
![alt text](https://github.com/kcwian/LineFollower/blob/master/imgs/app1.png)
- Configuration screen:
![alt text](https://github.com/kcwian/LineFollower/blob/master/imgs/app2.png)

## Robot

![alt text](https://github.com/kcwian/LineFollower/blob/master/imgs/linefollower.jpg)

## Video