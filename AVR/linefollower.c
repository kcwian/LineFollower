/*
 * linefollower.c
 *
 *  Created on: 18 mar 2016
 *      Author: Krzysztof Ćwian
 *-
 *
 */

/* Output - PIN
 * LED - PD7 -
 * Motor Right - PB1 -
 * Motor Left - PB2 -
 * Sensors - PC0-PC5 
*/

#include "headers.h"


unsigned char actualADCChannel = 0;
unsigned char sensorsUpdate = 0; // Flag set after reading data from sensors
unsigned char cnt0 = 0, cnt0Manual = 0, cnt0Up = 110, cnt0Left = 110, cnt0Right = 110; // Timer0
unsigned char sensorsState = 0; // State of the sensors
unsigned char lastTurnDirection;
short lineError = 0;
char manualMode = 0; // 0 - "Auto', 1 - "Manual"

// Configuration values
struct MotorSpeedStruct MotorSpeed = {30,60,40};
unsigned char ADCthreshold = 20; // > x
unsigned char Kp = 2;
unsigned char dataReceived[6], dataReceivedIndex;




int main(void)
{
	//Set calculated values
	UBRRH = UBRRH_VALUE;
	UBRRL = UBRRL_VALUE;

	initialize();

	while(1)
	{
		if(sensorsUpdate == 1)
		{
			sensorsUpdate = 0;
			steeringAlgorithm();
		}
	}
}

ISR(TIMER0_OVF_vect) // Timer0 Interrupts | Called each  ~2ms
{

	if(manualMode == 1)			// Mode "Manual"
	{
		if(cnt0Manual++ == 100)
		{
			cnt0Manual = 0;
			LED_TOGGLE;
		}
		if (cnt0Up < 60)
		{
			cnt0Up++;
			setRightMotorSpeed(MotorSpeed.fast);
			setLeftMotorSpeed(MotorSpeed.fast);
		}
		else if (cnt0Left < 60)
		{
			cnt0Left++;
			setRightMotorSpeed(MotorSpeed.turn);
			setLeftMotorSpeed(0);
		}
		else if (cnt0Right < 60)
		{
			cnt0Right++;
			setRightMotorSpeed(0);
			setLeftMotorSpeed(MotorSpeed.turn);
		}
		else
		{
			setRightMotorSpeed(0);
			setLeftMotorSpeed(0);
		}
	}
	else  								// Mode "Auto:
	{
		if(cnt0++ == 255)
		{
			cnt0 = 0;
			if(sensorsState && sensorsState != 0x3F)
				LED_ON;
			else
				LED_TOGGLE;
		}
	}
}


ISR(ADC_vect) // ADC Conversion Complete Interrupt
{
	if(manualMode) return;
	unsigned char ADCValue = ADCH;	// Read ADC value

	if(ADCValue > ADCthreshold)		// Check if sensor detects line
		sensorsState |= (0x01<<actualADCChannel);
	else
		sensorsState &= ~(0x01<<actualADCChannel);

	actualADCChannel++;

	if(actualADCChannel > 5)
	{
		actualADCChannel = 0;
		sensorsUpdate = 1;				// Update of 6 sensors completed
	}

	setADCChannel(actualADCChannel);
	ADCSRA |= (1<<ADSC); // ADC Start Conversion
}

ISR(USART_RXC_vect) // USART, Rx Complete (Atmega8)
{

	dataReceived[dataReceivedIndex] = UDR;
	dataReceivedIndex++;

	if(dataReceivedIndex > 1 && dataReceived[0] == 121)
	{
		dataReceivedIndex = 0;
		UDR = 121;
		switch(dataReceived[1])
		{
		case 122:
			manualMode = 1;
			setRightMotorSpeed(0);
			setLeftMotorSpeed(0);
			break;
		case 123:
			manualMode = 0;
			setRightMotorSpeed(0);
			setLeftMotorSpeed(0);
			ADCSRA |= (1<<ADSC); // ADC Start Conversion
			break;
		case 8:
			cnt0Up = 0;
			break;
		case 4:
			cnt0Left = 0;
			break;
		case 6:
			cnt0Right = 0;
			break;
		}
	}

	else if(dataReceivedIndex > 5 && dataReceived[0] == 111)
	{
		dataReceivedIndex = 0;
		UDR = 111;
		Kp = dataReceived[1];
		ADCthreshold = dataReceived[2];
		MotorSpeed.slow = dataReceived[3];
		MotorSpeed.fast = dataReceived[4];
		MotorSpeed.turn = dataReceived[5];

	}
	else if(dataReceivedIndex > 5 && dataReceived[0] != 111)
	{
		dataReceivedIndex = 0;
		for(int i=0;i<6;i++)
			dataReceived[i] = 0;
		UDR = 100; 	// Error
	}

}
