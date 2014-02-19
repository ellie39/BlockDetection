import lejos.nxt.ColorSensor;
import lejos.nxt.ColorSensor.Color;
import lejos.nxt.Sound;
import lejos.util.Delay;

/**
 * 
 * @author skanet1
 *Block detection
 *beeps if it sees a particular color
 */
public class BlockDetection extends Thread{
	private final int MIN_DISTANCE = 25;
	private final int BLOCK_BLUE = 12;
	//private final int BLOCK_GREEN = 5;
	private UltrasonicPoller usPoller;
	private ColorSensor coSensor;
	private Color color;
	private Driver robot;
	private Object lock;
	
	private boolean seesBlock = false;
	private boolean seesObject = false;
	public BlockDetection(UltrasonicPoller usPoller, ColorSensor coSensor, Driver driver){
		this.coSensor = coSensor;
		this.usPoller = usPoller;
		this.lock = new Object();
		this.robot = driver;
	}
	public void run(){
		while(true){
			synchronized(lock){color = coSensor.getColor();}
			//Pauses thread
			if(usPoller.getDistance() < MIN_DISTANCE){
				seesObject = true;
				detectBlock();
				//Sound.beep();
/*				if(!seesBlock && avoid){
					robot.avoid();
					robot.travel(Lab5.xDest, Lab5.yDest);
				}*/
			}
			else {
				seesObject = false;
				seesBlock = false;
			}
			try {Thread.sleep(50);} catch (InterruptedException e) {e.printStackTrace();}
		}
	}
	private void detectBlock(){
		//beeps if block is blue enough
		if(color.getBlue() > BLOCK_BLUE){			
			//Sound.beep();
			seesBlock = true;
		}
		else seesBlock = false;
	}
	public boolean seesBlock(){
		boolean boo;
		synchronized(lock){ boo = seesBlock;}
		return boo;
	}
	public Color getColor(){
		Color col;
		synchronized(lock){ col = color;}
		return col;
	}
	public int getBlue() {
		int blue;
		synchronized(lock){ blue = color.getBlue();}
		return blue;
	}
	public boolean seesObject() {
		boolean boo;
		synchronized(lock){ boo = seesObject;}
		return boo;
	}
}
