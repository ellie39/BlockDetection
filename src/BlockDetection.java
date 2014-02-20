import lejos.nxt.ColorSensor;
import lejos.nxt.ColorSensor.Color;
import lejos.nxt.Sound;
import lejos.util.Delay;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * 
 * @author skanet1
 *Block detection
 *beeps if it sees a particular color
 */
public class BlockDetection implements TimerListener{
	private final int MIN_DISTANCE = 20;
	private final int BLOCK_BLUE = 7;
	private static final int TIMER_PERIOD = 50;
	//private final int BLOCK_GREEN = 5;
	private UltrasonicPoller usPoller;
	private ColorSensor coSensor;
	private Color color;
	private Driver robot;
	private Object lock;
	private Timer timer;
	private boolean seesBlock = false;
	private boolean seesObject = false;
	public BlockDetection(UltrasonicPoller usPoller, ColorSensor coSensor, Driver driver){
		this.coSensor = coSensor;
		this.usPoller = usPoller;
		this.lock = new Object();
		this.robot = driver;
		this.timer = new Timer(TIMER_PERIOD, this);
		
		timer.start();
	}
	@Override
	public void timedOut() {
		synchronized(lock){color = coSensor.getColor();}
		if(usPoller.getDistance() < MIN_DISTANCE){
			seesObject = true;
			detectBlock();
		}
		else {
			seesObject = false;
			seesBlock = false;
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
