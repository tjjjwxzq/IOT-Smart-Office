#include <Stepper.h>

// LDR connected to A0
// 28BYJ-48 Stepper Motor connected to pins 8, 9, 10, 11

const int stepsPerRevolution = 2048; 

Stepper myStepper(stepsPerRevolution, 8, 10, 9, 11);        
int lightValue = 0;

void setup() {
  lightValue = analogRead(A0);
  Serial.println(lightValue)
  myStepper.setSpeed(4);
  Serial.begin(9600);
}

void loop() {
  value = analogRead(A0);
  Serial.println(value);
   Serial.println("clockwise");
  myStepper.step(-5*stepsPerRevolution);
  delay(1000);
  
  Serial.println("counterclockwise");
  myStepper.step(5*stepsPerRevolution);
  delay(1000); 
}


