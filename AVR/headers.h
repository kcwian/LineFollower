/*
 * headers.h
 *
 *  Created on: 18 mar 2016
 *      Author: Krzysztof Ćwian
 */

#ifndef HEADERS_H_
#define HEADERS_H_

# define F_CPU 8000000UL  

#include <stdio.h>
#include <math.h>
#include <avr/io.h>
#include <stdlib.h>
#include <util/delay.h>
#include <avr/interrupt.h>
#define BAUD 9600         // Bluetooth transmittion baudrate
#include <util/setbaud.h> // Linking after BAUD definied

#define LED_ON (PORTD |= (1<<PD7))
#define LED_OFF (PORTD &= ~(1<<PD7))
#define LED_TOGGLE (PORTD ^= (1<<PD7))

struct MotorSpeedStruct
{
	short slow;
	short fast;
	short turn;
};

struct MotorSpeedStruct MotorSpeed;
unsigned char sensorsState; // State of sensors (6 bits)
unsigned char lastTurnDirection;
unsigned char Kp;
unsigned char ADCthreshold; // > x
short lineError;

enum {LEFT,RIGHT};
void initialize();
void setADCChannel(char channel); // Sets ADC 0-5 channel
void setRightMotorSpeed(short val); //0-255
void setLeftMotorSpeed(short val); //0-255
char getLinePosition(unsigned char sensorsState);
short getLineError(unsigned char sensorsState); 
void steeringAlgorithm();




#endif /* HEADERS_H_ */
