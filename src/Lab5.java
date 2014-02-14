import lejos.nxt.*;
import lejos.util.Delay;

public class Lab5 {
	public static void main(String[] args) {
		// setup the pollers
		UltrasonicPoller usPoller = new UltrasonicPoller(new UltrasonicSensor(SensorPort.S2));
		ColorSensor cs = new ColorSensor(SensorPort.S1);
		cs.setFloodlight(lejos.robotics.Color.RED);
		Odometer odo = new Odometer();
		BlockDetection blockDetector =  new BlockDetection(usPoller, cs);
		
		Driver driver = new Driver(odo);
		blockDetector.start();
		OdometryDisplay lcd = new OdometryDisplay(odo, blockDetector);
		odo.start();
		lcd.start();
		
		Button.waitForAnyPress();
		NXTRegulatedMotor armMotor = Motor.C;
		armMotor.forward();
		armMotor.setSpeed(150);
		armMotor.rotate(-75, true);
		Delay.msDelay(250);
		armMotor.stop();
		// perform the ultrasonic localization
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}

}
