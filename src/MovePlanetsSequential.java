import java.util.List;
import java.util.concurrent.Semaphore;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class MovePlanetsSequential extends Thread {
	
	private static final int NUM_BODIES = 2;
	private static final float G = (float) (6.67*Math.pow(10, -6));
	private static final float DT = .005f;
	
	private static GraphicsContext gc;
	
	private static int numThreads;	// number of worker threads
	private static int numBodies;	// number of bodies
	private static float radius;	// radius of each body
	private static int timeSteps;	// time steps for completion
	
	
	public void setGC(GraphicsContext gc) {
		MovePlanetsSequential.gc = gc;
	}
	
	public void setArgs(List<String> args) {
		numThreads = Integer.parseInt(args.get(0));
		numBodies = Integer.parseInt(args.get(1));
		radius = Float.parseFloat(args.get(2));
		timeSteps = Integer.parseInt(args.get(3));
	}
	
	static Point bodyPos[];
	static Point bodyVelo[];
	static Point bodyForce[];
	static float bodyRadius[];
	static long bodyMass[];
	
	
	/**
	 * A dissemination barrier that uses a semaphore array to block all of the 
	 * threads until they all arrive.
	 * @param id - the id of the worker which could be any
	 * integer 0 to 31.
	 */
	/*
	public static void DBarrier(int id) {
		
		int stage = 1;			// int that doubles so we loop max 5 times for 32 threads
		int level = 0;			// keeps track of actual iteration we are on
		
		while(stage < numThreads){
			// V operation on this level's (id+stage)%numThread sem
			semArray[level][(id + stage) % numThreads].release();
			
			// P operation on this level's my sem
			try {
				semArray[level][id].acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// double stage
			stage = stage * 2;
			
			// increase iteration
			level = level + 1;
		}
	}
	*/
	
	/*
	public static void initBodies() {
		
		bodyPos   	= new Body[numBodies];
		bodyVelo 	= new Body[numBodies];
		bodyForce	= new Body[numBodies];
		bodyRadius 	= new float[numBodies];
		bodyMass	= new long[numBodies];
		
		for(int i = 0; i < NUM_BODIES; i++) {
			//bodyPos[i] 	 = new Body((i+1)*100, (i+1)*100);
			bodyVelo[i]  = new Body(0, 0);
			bodyForce[i] = new Body(0, 0);
			bodyRadius[i] = 50;
			bodyMass[i]  = 999999999999L;
		}
		bodyPos[0] = new Body(100, 200);
		bodyPos[1] = new Body(700, 200);
		//bodyPos[2] = new Body(300, 300);
		//bodyPos[3] = new Body(200, 300);
	}
	*/
	
	public static void initBodies() {

		bodyPos   	= new Point[numBodies];
		bodyVelo 	= new Point[numBodies];
		bodyForce	= new Point[numBodies];
		bodyRadius 	= new float[numBodies];
		bodyMass	= new long[numBodies];

		for(int i = 0; i < numBodies; i++) {
			//bodyPos[i] 	 = new Body((i+1)*100, (i+1)*100);
			bodyVelo[i]  = new Point(0, 0);
			bodyForce[i] = new Point(0, 0);
			bodyRadius[i] = 50;
			bodyMass[i]  = 9999999999999L;
		}
		bodyPos[0] = new Point(100, 200);
		bodyPos[1] = new Point(300, 200);
		bodyPos[2] = new Point(500, 200);
		//bodyPos[3] = new Point(400, 100);
		//bodyPos[4] = new Body(250, 250);
	}
	
	public static void calcForces() {
		float distance;
		float magnitude;
		Point direction = new Point(0, 0);
		for(int i = 0; i < numBodies-1; i++) {
			for(int j = i + 1; j < numBodies; j++) {
				distance = getDistance(bodyPos[i], bodyPos[j]);
				magnitude = (float) ((G * bodyMass[i] * bodyMass[j]) / Math.pow(distance, 2));
				direction.setX(bodyPos[j].x - bodyPos[i].x); 
				direction.setY(bodyPos[j].y - bodyPos[i].y);
				bodyForce[i].x = bodyForce[i].x + magnitude*direction.x/distance;
				bodyForce[j].x = bodyForce[j].x - magnitude*direction.x/distance;
				bodyForce[i].y = bodyForce[i].y + magnitude*direction.y/distance;
				bodyForce[j].y = bodyForce[j].y - magnitude*direction.y/distance;
			}
		}
	}
	
	public static void moveBodies() {
		Point deltav = new Point(0, 0);
		Point deltap = new Point(0, 0);
		
		for(int i = 0; i < numBodies; i++) {
			deltav.setX(bodyForce[i].x / bodyMass[i] * DT);
			deltav.setY(bodyForce[i].y / bodyMass[i] * DT);
			deltap.setX((bodyVelo[i].x + deltav.x/2) * DT);
			deltap.setY((bodyVelo[i].y + deltav.y/2) * DT);
			
			bodyVelo[i].x = bodyVelo[i].x + deltav.x;
			bodyVelo[i].y = bodyVelo[i].y + deltav.y;
			bodyPos[i].x = bodyPos[i].x + deltap.x;
			bodyPos[i].y = bodyPos[i].y + deltap.y;
			bodyForce[i].x = 0;
			bodyForce[i].y = 0;
		}
	}
	
	public static void calcColVelos() {
		
		for(int i = 0; i < numBodies-1; i++) {
			for(int j = i + 1; j < numBodies; j++) {
				if(getDistance(bodyPos[i], bodyPos[j]) <= (bodyRadius[i]+bodyRadius[j])){
					// there is a collision between i and j
					System.out.println(i + " collides with " + j);
					float denom = (float) (Math.pow(bodyPos[j].x - bodyPos[i].x, 2) + Math.pow(bodyPos[j].y - bodyPos[i].y, 2));
					
					float num1 = (float) (bodyVelo[j].x*Math.pow(bodyPos[j].x-bodyPos[i].x, 2)
							+ bodyVelo[j].y*(bodyPos[j].x-bodyPos[i].x)*(bodyPos[j].y-bodyPos[i].y));
					float num2 = (float) (bodyVelo[i].x*Math.pow(bodyPos[j].y-bodyPos[i].y, 2)
							+ bodyVelo[i].y*(bodyPos[j].x-bodyPos[i].x)*(bodyPos[j].y-bodyPos[i].y));
					
					float bvix = (num1 + num2) / denom;
					
					num1 = (float) (bodyVelo[j].x*(bodyPos[j].x-bodyPos[i].x)*(bodyPos[j].y-bodyPos[i].y)
							+ bodyVelo[j].y*Math.pow(bodyPos[j].y-bodyPos[i].y, 2));
					num2 = (float) (-1 * bodyVelo[i].x*(bodyPos[j].y-bodyPos[i].y)*(bodyPos[j].x-bodyPos[i].x)
							+ bodyVelo[i].y*Math.pow(bodyPos[j].x-bodyPos[i].x, 2));
					
					float bviy = (num1 + num2) / denom;
					
					num1 = (float) (bodyVelo[i].x*Math.pow(bodyPos[j].x-bodyPos[i].x, 2)
							+ bodyVelo[i].y*(bodyPos[j].x-bodyPos[i].x)*(bodyPos[j].y-bodyPos[i].y));
					num2 = (float) (bodyVelo[j].x*Math.pow(bodyPos[j].y-bodyPos[i].y, 2)
							+ bodyVelo[j].y*(bodyPos[j].x-bodyPos[i].x)*(bodyPos[j].y-bodyPos[i].y));
					
					float bvjx = (num1 + num2) / denom;
					
					num1 = (float) (bodyVelo[i].x*(bodyPos[j].x-bodyPos[i].x)*(bodyPos[j].y-bodyPos[i].y)
							+ bodyVelo[i].y*Math.pow(bodyPos[j].y-bodyPos[i].y, 2));
					num2 = (float) (-1 * bodyVelo[j].x*(bodyPos[j].y-bodyPos[i].y)*(bodyPos[j].x-bodyPos[i].x)
							+ bodyVelo[j].y*Math.pow(bodyPos[j].x-bodyPos[i].x, 2));
					
					float bvjy = (num1 + num2) / denom;
					
					bodyVelo[i].x = bvix;
					bodyVelo[i].y = bviy;
					bodyVelo[j].x = bvjx;
					bodyVelo[j].y = bvjy;
				}
			}
		}
	}
	
	private static float getDistance(Point body, Point body2) {
		float distance = (float) Math.sqrt(Math.pow(body.x - body2.x, 2) 
				+ Math.pow(body.y - body2.y, 2));
		return distance;
	}

	public static void drawBodies() {
		// clear screen
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, 800, 500);
		
		//gc.setStroke(Color.YELLOW);
		gc.setFill(Color.AQUA);
		
		for(int i = 0; i < numBodies; i++) {
			//gc.strokeOval(bodyPos[i].x, bodyPos[i].y, 2*bodyRadius[i], 2*bodyRadius[i]);
			gc.fillOval(bodyPos[i].x, bodyPos[i].y, 2*bodyRadius[i], 2*bodyRadius[i]);
		}
	}
	
	public void run() {
		System.out.println(numThreads);
		System.out.println(numBodies);
		System.out.println(radius);
		System.out.println(timeSteps);
		initBodies();
		drawBodies();
		int i = 0;
		while(i<9999999) {
			//System.out.println(i++);
			calcForces();
			moveBodies();
			
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			calcColVelos();
			drawBodies();
		}
	}
}
