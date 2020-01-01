/*
 * functions.c
 *
 *  Created on: 18 mar 2016
 *      Author: Krzysztof Ćwian
 */

#include "headers.h"

void initialize()
{
	//  Pull-up Inputs
	PORTB = 0xFF;
	PORTC = 0xFF;
	PORTD = 0xFF;
	DDRD |= (1<<PD7); // LED as output
	LED_OFF;
	DDRB |= (1<<PB1) | (1<<PB2); // Motors as output
	PORTB &= ~(1<<PB1) & ~(1<<PB2); // Motors turned off

	//ADC
	ADMUX |= (1<<REFS0); // AVCC with external capacitor at AREF pin
	ADMUX |= (1<<ADLAR); // ADC Left Adjust Result - ADCH = 8bit
	ADCSRA |= (1<<ADEN); // ADC Enable
	ADCSRA |= (1<<ADIE); // ADC Interrupt Enable
	ADCSRA |=  (1<<ADPS2); // CLK/16
	ADCSRA |= (1<<ADSC); // ADC Start Conversion

	//TIMER0

	TCCR0 |= (1<<CS01) | (1<<CS00); // clk/64 (From prescaler)
	TIMSK |= (1<<TOIE0); // Overflow interrupt enabled

	//TCCR0B |= (1<<CS01) | (1<<CS00); // clk/64 (From prescaler)  // Atmega328p
	//TIMSK0 |= (1<<TOIE0); // Overflow interrupt enabled		   // Atmega328p

	//TIMER1

	TCCR1B |= (1<<CS10); // clk/1
	//TIMSK |= (1<<TOIE1); // Overflow Enterrupt Enable
	//TIMSK |= (1<<OCIE1A); // Output Compare Match A Interrupt Enable
	//TIMSK |= (1<<OCIE1B); // Output Compare Match B Interrupt Enable

	TCCR1A |= (1<<COM1A1); // Clear OC1A/OC1B on Compare Match, set OC1A/OC1B at BOTTOM (non-inverting mode)
	TCCR1A |= (1<<COM1B1); // Clear OC1A/OC1B on Compare Match, set OC1A/OC1B at BOTTOM (non-inverting mode)
	TCCR1A |= (1<<WGM10); // Fast PWM, 8-bit
	TCCR1B |= (1<<WGM12); // Fast PWM, 8-bit

	//UART

	UCSRB |= (1<<TXEN); // Transmitter Enable
	UCSRB |= (1<<RXEN); // Receiver Enable
	UCSRB |= (1<<RXCIE); // RX Complete Interrupt Enable
	//UCSRB |= (1<<TXCIE); // TX Complete Interrupt Enable

	sei();
}

void setADCChannel(char channel)
{
	ADMUX &= 0xF0; // Sets 0-3 bits to zero

	switch(channel)
	{
	case 0:
		break;
	case 1:
		ADMUX |= (1<<MUX0);
		break;
	case 2:
		ADMUX |= (1<<MUX1);
		break;
	case 3:
		ADMUX |= (1<<MUX0) | (1<<MUX1);
		break;
	case 4:
		ADMUX |= (1<<MUX2);
		break;
	case 5:
		ADMUX |= (1<<MUX2) | (1<<MUX0);
		break;
	}
}

void setRightMotorSpeed(short val)
{
	//val += 5;
	if(val >= 0 && val <= 255)
		OCR1A = val;
	else if(val > 255)
		OCR1A = 255;
	else if(val < 0)
		OCR1A = 0;
}
void setLeftMotorSpeed(short val)
{
	if(val >= 0 && val <= 255)
		OCR1B = val;
	else if(val > 255)
		OCR1B = 255;
	else if(val < 0)
		OCR1B = 0;
}



short getLineError(unsigned char sensorsState)
{
	char detected = 0;
	short returnValue = 0;
	for(char i=0;i<6;i++)
	{
		if(sensorsState & (1<<i)) // If sensor detects line
		{
			detected++;
			if(i<3)
				returnValue += (i-2);
			else
				returnValue += (i-3);
		}
	}
	if(detected)
		return (returnValue*6/detected);
	else
		return 0;
}

void steeringAlgorithm()
{

	// Setting new values
	if(sensorsState == 0b00111111)			// If all sensors detect black line
	{
		setRightMotorSpeed(0);
		setLeftMotorSpeed(0);
	}
	else if(sensorsState == 0b00000000 )	// If all sensors detect white background
	{
		if(lastTurnDirection == LEFT)
		{
			setRightMotorSpeed(MotorSpeed.turn);
			setLeftMotorSpeed(0);
		}
		else if(lastTurnDirection == RIGHT)
		{
			setRightMotorSpeed(0);
			setLeftMotorSpeed(MotorSpeed.turn);
		}
	}
	else
	{
		lineError = getLineError(sensorsState); // Calculating error

		// Accelerate / Deccelerate
		if(lineError < 3 && lineError > -3)		// If error is small then accelerate
		{
			setRightMotorSpeed(MotorSpeed.fast + lineError*Kp);
			setLeftMotorSpeed(MotorSpeed.fast - lineError*Kp);
		}
		else									// Otherwise deccelerate
		{
			setRightMotorSpeed(MotorSpeed.slow + lineError*Kp);
			setLeftMotorSpeed(MotorSpeed.slow - lineError*Kp);
		}

		if(lineError > 0)										// Saving last turn direction
			lastTurnDirection = LEFT;
		else if(lineError < 0)
			lastTurnDirection = RIGHT;
	}
}
