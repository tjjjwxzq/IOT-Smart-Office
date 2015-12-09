/*
Connect Bluetooth RX,TX to Arduino RX,TX
Requires NeoPixel Library
https://github.com/adafruit/Adafruit_NeoPixel
If you want to view via Serial Monitor and have your Arduino connect to BT module 
simultaneously use Software Serial to open a second port.
#include <SoftwareSerial.h>
SoftwareSerial mySerial(3, 4); // RX, TX
*/



#include <SoftwareSerial.h>

SoftwareSerial mySerial(10, 11); // RX, TX

unsigned long previousTime = 0; // last time alert is sent
unsigned long currentTime; // check current time
const long interval = 5000; // interval at which to send alerts 

void setup() {
 
  // initialize serial:
   Serial.begin(9600);
   mySerial.begin(9600);
   pinMode(13,OUTPUT);
   
   Serial.println("Test starting");
}

void loop() {
  
  currentTime = millis(); //get time since board started

  if(currentTime - previousTime >= interval)
  {
    previousTime = currentTime;

    mySerial.print("ALERT");


  }
  // if there's any serial available, read it:
  while (mySerial.available() > 0) {

    //light LED if 1 is received
    if (mySerial.read() == '1')
    {
      
      Serial.println("LED ON");
      mySerial.print("LED ON");
      digitalWrite(13, HIGH);
      delay(1000);
      digitalWrite(13, LOW);
      delay(1000);
    }
}
}

