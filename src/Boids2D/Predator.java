package Boids2D;

import java.awt.Point;

public class Predator extends Boid{
	public Predator(){
		this(new Point((int)(Math.random()*1000-500),(int)(Math.random()*1000-500)));
	}
	public Predator(Point sLoc){
		setLoc(sLoc);
		setVel(new Point(0,0));
		setAcc(1);
	}
	
}
