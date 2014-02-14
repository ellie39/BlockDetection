import lejos.nxt.ColorSensor;
import lejos.nxt.ColorSensor.Color;
import lejos.nxt.Sound;

/**
 * 
 * @author skanet1
 *Block detection
 *beeps if it sees a particular color
 */
public class BlockDetection extends Thread{
	private final int MIN_DISTANCE = 30;
	private final int BLOCK_COLOR = 50;
	private UltrasonicPoller usPoller;
	private ColorSensor coSensor;
	private Color color;
	private Object lock;
	
	private boolean seesBlock = false;
	private boolean seesObject = false;
	public BlockDetection(UltrasonicPoller usPoller, ColorSensor coSensor){
		this.coSensor = coSensor;
		this.usPoller = usPoller;
		this.lock = new Object();
	}
	public void run(){
		while(true){
			synchronized(lock){color = coSensor.getColor();}
			//Pauses thread
			if(usPoller.getDistance() < MIN_DISTANCE){
				seesObject = true;
				detectBlock();
			}
			else seesObject = false;
			try {Thread.sleep(50);} catch (InterruptedException e) {e.printStackTrace();}
		}
	}
	private void detectBlock(){
		//beeps if block is blue enough
		if(color.getBlue() > BLOCK_COLOR){
			Sound.beep();
			seesBlock = true;
		}
		else seesBlock = false;
	}
	public boolean seesBlock(){
		boolean boo;
		synchronized(lock){ boo = seesBlock;}
		return boo;
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
