/* FSR testing sketch. 
 
Connect one end of FSR to 5V, the other end to Analog 0.
Then connect one end of a 10K resistor from Analog 0 to ground
Connect LED from pin 11 through a resistor to ground 
 
For more information see www.ladyada.net/learn/sensors/fsr.html */
 
int fsrAnalogPin = 0; // FSR is connected to analog 0
int motorPin = 3;      // connect Red LED to pin 11 (PWM pin)
int fsrReading;      // the analog reading from the FSR resistor divider
int motorSpeed;
unsigned long previousTime; // time since user last got up
unsigned long currentTime;
unsigned long previousBuzzTime;  // time since motor started buzzing
int timeInterval;
int buzzDuration;
  
void setup(void) {
  Serial.begin(9600);   // We'll send debugging information via the Serial monitor
  pinMode(motorPin, OUTPUT);
  previousTime = 0;
  timeInterval = 10000; // 10 seconds
  buzzDuration = 3000; // buzz for 3 seconds before stopping
}
 
void loop(void) {
  
  // we'll need to change the range from the analog reading (0-1023) down to the range
  // used by analogWrite (0-255) with map!
  motorSpeed = map(fsrReading, 0, 1023, 0, 255);
  fsrReading = analogRead(fsrAnalogPin);
  currentTime = millis();
   
  // MOTOR goes faster the harder you press
  if (fsrReading > 850){ 
    Serial.println("Sitting"); 
    Serial.print("current ");
    Serial.println(currentTime);
    Serial.print("previous");
    Serial.println(previousTime);
    if(currentTime - previousTime >= timeInterval){
       previousTime = currentTime;
       previousBuzzTime = currentTime;
       while(currentTime - previousBuzzTime <= buzzDuration) 
       {
         currentTime = millis();
         analogWrite(motorPin, motorSpeed);
       }
    } else {
          analogWrite(motorPin, 0);
    } 
     
  }else 
  {
    previousTime = currentTime;  
    analogWrite(motorPin, motorSpeed);    
  }
 
  delay(100);
}
