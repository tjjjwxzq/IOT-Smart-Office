/* FSR testing sketch. 
 
Connect one end of FSR to 5V, the other end to Analog 0.
Then connect one end of a 10K resistor from Analog 0 to ground
Connect LED from pin 11 through a resistor to ground 
 
For more information see www.ladyada.net/learn/sensors/fsr.html */
 
int fsrAnalogPin = 0; // FSR is connected to analog 0
int MOTORpin = 11;      // connect Red LED to pin 11 (PWM pin)
int fsrReading;      // the analog reading from the FSR resistor divider
int MotorSpeed;
double Timer;
double BuzzTimer;
double lastSittingDur;
double BuzzLimit;
double FirstBuzzTime;
double BuzzCycle;
 
void setup(void) {
  Serial.begin(9600);   // We'll send debugging information via the Serial monitor
  pinMode(MOTORpin, OUTPUT);
  Timer = 0;
  BuzzTimer = 0;
  lastSittingDur = 0;
  BuzzLimit = 3;
  BuzzCycle = 10;
  FirstBuzzTime = 10;
}
 
void loop(void) {
  fsrReading = analogRead(fsrAnalogPin);
  Serial.print("Analog reading = ");
  Serial.print(fsrReading);
  Serial.print(", Timer = ");
  Serial.print(Timer);
  Serial.print(", Last Sitting Duration = ");
  Serial.println(lastSittingDur);
 
  // we'll need to change the range from the analog reading (0-1023) down to the range
  // used by analogWrite (0-255) with map!
  MotorSpeed = map(fsrReading, 0, 1023, 0, 255);
  // MOTOR goes faster the harder you press
  if (fsrReading > 850 && Timer >= 10){
     if (BuzzTimer <= BuzzCycle){
        BuzzTimer= BuzzTimer + 0.1; 
     } else {
        BuzzTimer=0; 
     }
     Timer = Timer + 0.1;
     if (BuzzTimer <= BuzzLimit) {
        analogWrite(MOTORpin, fsrReading);
     } else {
        analogWrite(MOTORpin, 0);
     } 
  } else if (fsrReading >= 850) {
     analogWrite(MOTORpin, 0);
     Timer = Timer + 0.1; 
  } else {
     analogWrite(MOTORpin, 0);
     if (Timer > FirstBuzzTime) {
        lastSittingDur = Timer;
        Timer = 0;
     } else {
        Timer = 0;
     }
  }
 
  delay(100);
}
