import lejos.nxt.*;
import lejos.util.Delay;

public class Lab5 {
	public static double xDest = 30;
	public static double yDest = 150;
	public static Driver driver;
	public static BlockDetection blockDetector;
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
			
/*			USLocalizer usLocalizer = new USLocalizer(odo, driver, usPoller, USLocalizer.LocalizationType.FALLING_EDGE);
			usLocalizer.doLocalization();*/
			
			//Travel doesn't block anymore, so Immiediate Return occurs
			driver.travel(xDest, yDest);
			//allows blocks to be detected
			while(true){
				//avoids if object
				if(blockDetector.seesObject() && !blockDetector.seesBlock()){
					Sound.beep();
					Delay.msDelay(100);
					avoidBlock();
					if(!blockDetector.seesObject() || blockDetector.seesBlock()){
						driver.travel(Lab5.xDest, Lab5.yDest);
					}
				}
				//buzzes if block
				if(blockDetector.seesBlock()){
					Sound.beep();
					Delay.msDelay(100);
					Sound.beep();
					Delay.msDelay(100);
					getBlock();
				}
			}
		}
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
	public static void avoidBlock(){
		Sound.buzz();
		driver.stop();
		Delay.msDelay(1000);
		driver.goBackward(10);
		driver.turnTo(90);
		if(blockDetector.seesBlock() || !blockDetector.seesObject()){
			driver.goForward(20, false);
			driver.turnTo(-120);
			if(blockDetector.seesObject()){
				avoidBlock();
			}
		}
	}
	public static void getBlock(){
		
	}
}
