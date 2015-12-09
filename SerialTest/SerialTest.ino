/*
  DigitalReadSerial
 Reads a digital input on pin 2, prints the result to the serial monitor 
 
 This example code is in the public domain.
 */

// the setup routine runs once when you press reset:
void setup() {
  // initialize serial communication at 9600 bits per second:
  Serial.begin(9600);
  pinMode(13,OUTPUT);
}

// the loop routine runs over and over again forever:
void loop() {
  // read the input pin:
  if(Serial.available())
  {
     char ser = Serial.read();
     Serial.println(ser);
     if(ser == '1')
     {
       digitalWrite(13,HIGH);  
       delay(250);
       digitalWrite(13,LOW);
       delay(250);  
     }
     
     digitalWrite(13,HIGH);
     delay(1000);
     digitalWrite(13,LOW);
     delay(1000);
  }
  
}



