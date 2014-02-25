import lejos.nxt.*;
import lejos.util.Delay;

public class Lab5 {
	public static double xDest = 25;
	public static double yDest = 150;
	public static Driver driver;
	public static BlockDetection blockDetector;
	public static boolean hasBlock = false;
	public static void main(String[] args) {
		// setup the pollers
		UltrasonicPoller usPoller = new UltrasonicPoller(new UltrasonicSensor(SensorPort.S2));
		ColorSensor cs = new ColorSensor(SensorPort.S1);
		//cs.setFloodlight(lejos.robotics.Color.RED);
		Odometer odo = new Odometer();
		driver = new Driver(odo);
		blockDetector =  new BlockDetection(usPoller, cs, driver);
		OdometryDisplay lcd = new OdometryDisplay(odo, blockDetector, usPoller);
		odo.start();

		int buttonChoice;
		do {
			// clear the display
			LCD.clear();

			// ask the user whether the motors should Avoid Block or Go to locations
			LCD.drawString("< Left | Right >", 0, 0);
			LCD.drawString("       |        ", 0, 1);
			LCD.drawString(" Detect| Drive  ", 0, 2);
			LCD.drawString(" Blocks|        ", 0, 3);
			LCD.drawString("       |        ", 0, 4);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);
		lcd.start();
		if(buttonChoice == Button.ID_RIGHT){
			USLocalizer usLocalizer = new USLocalizer(odo, driver, usPoller, USLocalizer.LocalizationType.FALLING_EDGE);
			usLocalizer.doLocalization();
			
			//Travel doesn't block anymore, so Immiediate Return occurs
			driver.travel(xDest, yDest);
			//avoidance
			while((odo.getY() < 145)){
				//avoids if object
				if(blockDetector.seesObject()){
					driver.stop();
					//beeps that it sees an object
					Sound.beep();
					Delay.msDelay(100);
					//goes forward to improve accuracy of light sensor
					driver.goForward(2, false);
					//beeps and gets block if it sees one
					if(blockDetector.seesBlock()){
						Sound.beep();
						Delay.msDelay(100);
						getBlock();
					} else {
						//obstacle avoidance
						avoidBlock();
					}
					if(!blockDetector.seesObject() || blockDetector.seesBlock()){
						driver.travel(Lab5.xDest, Lab5.yDest);
					}
				}
			}
			//searches for block
			driver.rotate(true);
			while(!hasBlock){
				if(usPoller.getDistance() < 40){
					driver.goForward(22, false);
					driver.stop();
					Delay.msDelay(500);
					if(blockDetector.seesBlock()){
						getBlock();
					} else {
						driver.goBackward(22);
						driver.rotate(true);
					}
				} else {
					driver.rotate(true);
				}
				Delay.msDelay(500);
			}
		}
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
	public static void avoidBlock(){
		driver.stop();
		Sound.buzz();
		Delay.msDelay(1000);
		driver.turnTo(90);
		if(blockDetector.seesBlock() || !blockDetector.seesObject()){
			driver.goForward(30, false);
			driver.turnTo(-90);
			driver.goForward(15, false);
			driver.turnTo(-30);
			if(blockDetector.seesObject()){
				avoidBlock();
			}
		}
	}
	public static void getBlock(){
		hasBlock = true;
		driver.grab();
		driver.travel(70 ,190);
	}
}
