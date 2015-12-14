

#include <SoftwareSerial.h>

SoftwareSerial mySerial(10, 11); // RX, TX

int fsrAnalogPin = 0; // FSR is connected to analog 0
int motorPin = 3;      // connect Red LED to pin 11 (PWM pin)
int fsrReading;      // the analog reading from the FSR resistor divider
int motorSpeed;
unsigned long previousSitTime; // time since user last sat down
unsigned long previousUpTime; // time since user last got up
unsigned long previousBuzzTime;  // time since motor started buzzing
unsigned long currentTime;
int upTimeInterval;
int timeInterval;
int buzzDuration;
int alertLevel;
boolean sitting;

void setup() {
 
  // initialize serial:
   Serial.begin(9600);
   mySerial.begin(9600);
   pinMode(motorPin, OUTPUT);
   previousUpTime = -1;
   previousSitTime = 0;
   upTimeInterval = 5000; // 5 seconds
   timeInterval = 10000; // 10 seconds
   buzzDuration = 3000; // buzz for 3 seconds before stopping
   alertLevel = 1;
   sitting = false;
   Serial.println("Test starting");
}

void startAlerts(){
   while(true){
     
      // we'll need to change the range from the analog reading (0-1023) down to the range
      // used by analogWrite (0-255) with map!
     
      fsrReading = analogRead(fsrAnalogPin);
      currentTime = millis();
       
      // MOTOR goes faster the harder you press
      if (fsrReading > 850){ 
        Serial.println("Sitting"); 
        Serial.print("current ");
        Serial.println(currentTime);
        Serial.print("previous");
        Serial.println(previousSitTime);
        
        sitting = true;
        //User has sat for too long
        if(currentTime - previousSitTime >= timeInterval){
           
           //Send data to android app
           switch(alertLevel){
             case 1:
               mySerial.print("Alert Level 1");
               motorSpeed = 100; 
               break;
             case 2:
               mySerial.print("Alert Level 2");
               motorSpeed = 150;
               break;
             case 3:
               mySerial.print("Alert Level 3");
               motorSpeed = 200;
               break;
             case 4: 
               mySerial.print("Alert Level 4");
               motorSpeed = 250;
               break;  
           }
           
           previousUpTime = currentTime;
           previousSitTime = currentTime;
           previousBuzzTime = currentTime;
           while(currentTime - previousBuzzTime <= buzzDuration) 
           {
             currentTime = millis();
             analogWrite(motorPin, motorSpeed);            
           }
        } else {
           analogWrite(motorPin, 0);
            
           if(previousUpTime > 0 && (currentTime - previousUpTime)< upTimeInterval)
            {
             Serial.println("Didn't get up");   
             if(alertLevel<4)
               alertLevel+=1;
               Serial.println(alertLevel);
               mySerial.print("totalSittingDuration+"+timeInterval);
            }
          alertLevel = 1;
        } 
         
      }else 
      {
        
        if(previousUpTime > 0 && (currentTime - previousUpTime < upTimeInterval))
        {
          Serial.println("got up for too short");
            if(alertLevel<4)
              alertLevel+=1;
        }else
        {
          //User got up; get app increment sittingTimes
          mySerial.print("sittingTimes");
        }     
        previousSitTime = currentTime;
        analogWrite(motorPin, 0);    
      }
     
      delay(100);
    
    if(mySerial.readString().equals("Turn off alerts"))
    {
       previousUpTime = currentTime;
       break; 
    }
   }

}

void loop() {
  
  analogWrite(motorPin,0);  
  // if there's any serial available, read it:
  while (mySerial.available() > 0) {
    
    if(mySerial.readString().equals("Turn on alerts"))
     {
       Serial.println("Starting alerts");
       startAlerts();
     }
  }
}

