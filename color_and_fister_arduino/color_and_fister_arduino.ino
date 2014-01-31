#include <TimedAction.h>

#include <TimedAction.h>

#include <TimedAction.h>
#include <Servo.h>

#define FISTER_SERVO_PIN 6
#define PACMAN_SERVO_PIN 16

#define RED_LED_PIN 3
#define GREEN_LED_PIN 5
#define COLOR_SENSOR_INPUT 0

#define BREAK_BEAM_INPUT 1

#define VCC_PIN_COUNT 3
int VCC_PINS[VCC_PIN_COUNT] = {17, 8, 4 };

#define GROUND_PIN_COUNT 3
int GROUND_PINS[GROUND_PIN_COUNT] = {18, 9, 2};


#define BREAK_BEAM_THRESHOLD 250

#define PACMAN_NEUTRAL_POS 110
#define PACMAN_RED_POS 135
#define PACMAN_GREEN_POS 85
#define PACMAN_DISPENSE_DELAY 800

#define BALL_RED 1
#define BALL_GREEN 2
#define BALL_UNKNOWN 0

#define RED_THRESHOLD 8

#define FISTER_UP 120
#define FISTER_DOWN 0
#define FISTER_UP_DELAY 000
#define FISTER_DOWN_DELAY 400

#define FISTER_UP_INCREMENT_DELAY 3
#define FISTER_DOWN_INCREMENT_DELAY 3

TimedAction fisterAction = TimedAction(100,fisterMove);
TimedAction ballAction = TimedAction(10,ballCheck);

Servo pacman;
Servo fister;

int pos = 0;
int fist = 0;

int breakPin = 1;
int breakVal = 0;
int ledArray[] = {RED_LED_PIN, GREEN_LED_PIN};

int red = 0;
int green = 0;

int backgroundArray[] = {0,0};

void setup()
{
  pinMode(RED_LED_PIN, OUTPUT);
  pinMode(GREEN_LED_PIN, OUTPUT);
  
  for(int i=0; i < GROUND_PIN_COUNT; i++) {
    pinMode(GROUND_PINS[i], OUTPUT);
    digitalWrite(GROUND_PINS[i], LOW);
  }
  
  for(int i=0; i < VCC_PIN_COUNT; i++) {
    pinMode(VCC_PINS[i], OUTPUT);
    digitalWrite(VCC_PINS[i], HIGH);
  }
  
  pacman.attach(PACMAN_SERVO_PIN);
  fister.attach(FISTER_SERVO_PIN);
  
  Serial.begin(9600);
  pacman.write(PACMAN_NEUTRAL_POS);
  fister.write(FISTER_DOWN);
  delay(10);
  setBackground();
}

void loop()
{
  fisterAction.check();
  ballAction.check();
}

void ballCheck()
{
  if(checkBreak()) {
    delay(200);
    int decision = checkColor();
    printColor(decision);
    dispenseBall(decision);
  }
}


void setBackground()
{
  for (int i=0; i<=1; i++)
  {
    digitalWrite(ledArray[i],HIGH);
    delay(10);
    
    backgroundArray[i] = getReading(50);

    Serial.print("Background = ");
    Serial.println(int(backgroundArray[i]));
    digitalWrite(ledArray[i], LOW);
    delay(100);
    
  }
}

boolean checkBreak()
{
  breakVal = analogRead(breakPin);
  //Serial.println(breakVal);
  return breakVal < BREAK_BEAM_THRESHOLD;
}

int checkColor()
{
  int colorArray[2];
  int neutral = getReading(10);
  
  for(int i=0; i<=1; i++)
  {
    digitalWrite(ledArray[i],HIGH);
    delay(100);
    colorArray[i] = getReading(10);
    digitalWrite(ledArray[i],LOW);
    delay(100);
  }
  
  return chooseColor(backgroundArray, neutral, colorArray);
}

int getReading(int steps)
{
  int reading;
  int stepCount=0;
  for (int i=0; i<steps; i++)
  {
    reading = analogRead(0);
    stepCount = reading + stepCount;
    delay(10);
  }
  return (stepCount)/steps;
}

int chooseColor(int background[], int neutral, int brightness[])
{
  int value = (brightness[0] + brightness[1] - background[0] - background[1]);
  Serial.print("value: ");
  Serial.println(int(value));

  if(value > RED_THRESHOLD )
    return BALL_RED;
  return BALL_GREEN;
}

void printColor(int decision)
{
  switch(decision) {
    case BALL_GREEN:
    case BALL_UNKNOWN:
    Serial.println("GREEN");
    break;
    case 1:
    Serial.println("RED");
  }
}

void dispenseBall(int decision) {
  int servoPos;
  switch(decision) {
    case BALL_RED:
    servoPos = PACMAN_RED_POS;
    break;
    case BALL_GREEN:
    case BALL_UNKNOWN:
    servoPos = PACMAN_GREEN_POS;
  }
  pacman.write(servoPos);
  delay(PACMAN_DISPENSE_DELAY);
  pacman.write(PACMAN_NEUTRAL_POS);
}    

int fisterState = 0;
void fisterMove()
{
  switch(fisterState) {
  case 0:
    for(int i=FISTER_DOWN; i<=FISTER_UP; i++) {
      fister.write(i);
      delay(FISTER_UP_INCREMENT_DELAY);
    }
  break;
  case 1:
    delay(FISTER_UP_DELAY);
    break;
  case 2:
    for(int i=FISTER_UP; i>=FISTER_DOWN; i--) {
      fister.write(i);
      delay(FISTER_DOWN_INCREMENT_DELAY);
    }
    break;
  case 3:
    delay(FISTER_DOWN_DELAY);
    break;
  }
  fisterState = (fisterState + 1) & 0x3;
}
