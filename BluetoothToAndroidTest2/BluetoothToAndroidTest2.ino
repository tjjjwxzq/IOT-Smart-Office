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

unsigned long time1; // last time alert was set 
unsigned long time2; // next time led is on
unsigned long duration; //duration between led signal 

void setup() {
 
  // initialize serial:
   Serial.begin(9600);
   mySerial.begin(9600);
   pinMode(13,OUTPUT);
   
   time1=0;
   time2=0;
   
   Serial.println("Test starting");
}

void loop() {
  
  // if there's any serial available, read it:
  while (mySerial.available() > 0) {

    // look for the next valid integer in the incoming serial stream:
   // int red = mySerial.parseInt(); 
    // do it again:
   // int green = mySerial.parseInt(); 
    // do it again:
   // int blue = mySerial.parseInt(); 

    // look for the newline. That's the end of your
    // sentence:
     
    //Light LED if received 1
    if (mySerial.read() == '1')
    {
      time2 = millis(); //get time since board started
      duration = time2 - time1;
 
      Serial.println("LED ON");
      Serial.println(time2);
      Serial.println(time1);
      Serial.println(duration);
      
      time1 = time2;  
      
      mySerial.print("LED ON"); 
      digitalWrite(13, HIGH);
      delay(1000);
      digitalWrite(13, LOW);
      delay(1000);
    }
    // if (mySerial.read() == '\n') {
   // if (mySerial.read() == '\n') {
      // sends confirmation
   // Serial.println("received:" + String(red)+"," + String(green) + "," + String(blue));
    //Serial.write(red);
       
     /* // constrain the values to 0 - 255
      red = constrain(red, 0, 255);
      green = constrain(green, 0, 255);
      blue = constrain(blue, 0, 255);
      // fill strip
      //colorSet(strip.Color(red, green, blue), 0); 
      
      // send some data back
      mySerial.println("received:"+String(red)+","+String(green)+","+String(blue));
     
   }*/
}
}

