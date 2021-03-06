/**
 * Driver.java
 * 
 * The driver class used in our design
 * Controls all of the robot's movement
 */
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.util.Delay;


public class Driver extends Thread  {
	/* minimum speed the robot will travel at */
	private final int MIN_SPEED = 150;
	/* max speed the robot will travel at */
	private final int MAX_SPEED = 350;
	/*factor the error is multiplied by to calculate the speed*/
	private final int SCALING_FACTOR = 10;
	
	private static final int FORWARD_SPEED = 150;
	private static final int ROTATE_SPEED = 150;
	private static final int LOCALIZE_SPEED = 100;
	
	private static int speed;
	public static double xDest, yDest;
	
	NXTRegulatedMotor leftMotor = Motor.A;
	NXTRegulatedMotor rightMotor = Motor.B;
	NXTRegulatedMotor armMotor = Motor.C;
	
	private static double WHEEL_BASE = 15.5;
	private static double WHEEL_RADIUS = 2.16;
	
	public double thetar, xr, yr;
	private boolean navigating;
	private Odometer odo;
	public Driver(Odometer odometer){
		this.odo =  odometer;
		navigating = false;
	}
/**
 * Has the robot move to a position, relative to starting coordinates
 * 
 * Calculates angle and distance to move to using basic trig and then calls
 * the turnTo and goForward method to move to that point
 * 
 * @param X Coordinate of destination
 * @param Y Coordinate of destination
 */
	public void travel (double x, double y){
			xDest = x;
			yDest = y;
		//gets position. Synchronized to avoid collision
			synchronized (odo.lock) {
				thetar = odo.getTheta() * 180 / Math.PI;
				xr = odo.getX();
				yr = odo.getY();
			}
			//calculates degrees to turn from 0 degrees
			double thetad =  Math.atan2(x - xr, y - yr) * 180 / Math.PI;
			//calculates actual angle to turn
			double theta =  thetad - thetar;
			//calculates magnitude to travel
			double distance  = Math.sqrt(Math.pow((y-yr), 2) + Math.pow((x-xr),2));
			//finds minimum angle to turn (ie: it's easier to turn +90 deg instead of -270)
			if(theta < -180){
				turnTo(theta + 360);
			}
			else if(theta > 180){
				turnTo(theta - 360);
			}
			else turnTo(theta);
			//updates values to display
			goForward(distance);
	}
	//doesn't block
	public void goForward(double distance){
		
		// drive forward
		speed = FORWARD_SPEED;
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);
		
		//for isNavigatingMethod
		navigating = true;
		
		leftMotor.rotate(convertDistance(WHEEL_RADIUS, distance), true);
		rightMotor.rotate(convertDistance(WHEEL_RADIUS, distance), true);
		
		navigating = false;
	}
	public void goForward(double distance, boolean returnImmediately){
		
		// drive forward
		speed = FORWARD_SPEED;
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);
		
		//for isNavigatingMethod
		navigating = true;
		
		leftMotor.rotate(convertDistance(WHEEL_RADIUS, distance), true);
		rightMotor.rotate(convertDistance(WHEEL_RADIUS, distance), returnImmediately);
		
		navigating = false;
	}
	public void goBackward(double distance){
		// drive forward
		speed = FORWARD_SPEED;
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);
		Sound.beep();
		//for isNavigatingMethod
		navigating = true;
		
		leftMotor.rotate(-convertDistance(WHEEL_RADIUS, distance), true);
		rightMotor.rotate(-convertDistance(WHEEL_RADIUS, distance), false);
		
		navigating = false;
	}
	public void turnTo (double theta){
	
		// turn degrees clockwise
		leftMotor.setSpeed(LOCALIZE_SPEED);
		rightMotor.setSpeed(LOCALIZE_SPEED);
		
		navigating = true;
		//calculates angel to turn to and rotates
		leftMotor.rotate(convertAngle(WHEEL_RADIUS, WHEEL_BASE, theta), true);
		rightMotor.rotate(-convertAngle(WHEEL_RADIUS, WHEEL_BASE, theta), false);
		
		navigating = false;
	}
	public void rotate (boolean forward){
		leftMotor.setSpeed(LOCALIZE_SPEED);
		rightMotor.setSpeed(LOCALIZE_SPEED);
		if (forward){
			leftMotor.forward();
			rightMotor.backward();
		} else { 
			leftMotor.backward();
			rightMotor.forward();
		}
	}
	public void grab(){
		this.goForward(8, false);
		armMotor.forward();
		armMotor.setSpeed(150);
		armMotor.rotate(-120, false);
		Delay.msDelay(250);
		armMotor.stop();
	}
	public void stop(){
		speed = leftMotor.getSpeed();
		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);
	}
	public void resume(){
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);
	}
/**
 * Returns true if the robot is navigating
 * 
 * @return boolean indicating if the robot is traveling
 */
	public boolean isNavigating(){
		return this.navigating;
	}
/**
 * Returns degrees to turn servos in order to rotate robot by that amount
 * 
 * Uses basic math to convert and absolute angle to degrees to turn.
 * 
 * @param Radius of lego wheel
 * @param Width of wheel base
 * @param Absolute angle to turn to
 * 
 * @return Degrees the servo should turn
 */
	public static int convertAngle(double radius, double width, double angle) {
		//(width * angle / radius ) / (2)
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
/**
 * Moves robot linerly a certain distance
 * 
 * @param Radius of lego wheel
 * @param Distance to travel
 * 
 * @return degrees to turn servos in order to move forward by that amount
 */
	public static int convertDistance(double radius, double distance) {
		// ( D / R) * (360 / 2PI)
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
}
