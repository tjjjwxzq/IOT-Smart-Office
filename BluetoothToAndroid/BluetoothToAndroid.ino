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

char inData[20]; // Allocate space for string to be read in from serial
char inChar= -1; // to store the character read
byte index = 0; // where to store the character in the array

void setup() {
 
  // initialize serial:
   Serial.begin(9600);
   mySerial.begin(9600);
   pinMode(13,OUTPUT);
   
   Serial.println("Test starting");
}

char Comp(char* cmpChar){
  while(Serial.available() > 0)
  {
    if(index<19)
    {
      inChar = Serial.read();
      inData[index] = inChar;
      index++; 
      inData[index] = '\0'; //Null terminate the string 
    }
  }

  if (strcmp(inData,cmpChar) == 0)
  {
     for(int i =0; i <19; i++)
    {
       inData[i] = 0;
    }
    index = 0;
    return(0); 
  }
  else{
    return(1);
  }
}

void loop() {
  
  currentTime = millis(); //get time since board started

  if(currentTime - previousTime >= interval)
  {
    previousTime = currentTime;
    Serial.println("Alert");
    mySerial.print("ALERT");


  }
  // if there's any serial available, read it:
  while (mySerial.available() > 0) {
    Serial.println(Comp("Turn on alerts"));
    if(mySerial.read() == 'Turn on alerts')
     {
       Serial.println("Alert2");
     }
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

