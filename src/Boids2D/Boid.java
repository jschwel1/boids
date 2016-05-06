package Boids2D;

import java.awt.Point;
import java.awt.geom.Point2D;

public class Boid {
	// ================== INSTANCE FIELDS & CONSTANTS ============ //
	public static final int TERM_VEL = 40;
	public static final int MIN_VEL = 20;
	private int personalSpace;
	private Point2D loc;
	private Point2D vel;
	private Point2D lastVel;
	private double acc;
	

	// ================== CONSTRUCTORS ============ //
	public Boid(){
		this(new Point((int)(Math.random()*1000-500),(int)(Math.random()*1000-500)));
	}
	public Boid(Point sLoc){
		setLoc(sLoc);
		setVel(new Point(0,0));
		setAcc(1);
		setPersonalSpace(150);
		setLastVel(new Point((Point) vel));
	}
	// ================== GETTERS/SETTERS ============ //
	public Point2D getLoc() {
		return loc;
	}
	public void setLoc(Point loc) {
		this.loc = loc;
	}
	public Point2D getVel() {
		return vel;
	}
	public void setVel(Point vel) {
		this.vel = vel;
	}
	public double getAcc() {
		return acc;
	}
	public void setAcc(double acc) {
		this.acc = acc;
	}
	public int getPersonalSpace() {
		return personalSpace;
	}
	public void setPersonalSpace(int personalSpace) {
		this.personalSpace = personalSpace;
	}
	public Point2D getLastVel() {
		return lastVel;
	}
	public void setLastVel(Point2D lastVel) {
		this.lastVel = lastVel;
	}

	// ================== METHODS ============ //
	public int getDistance(Boid other){
		int x = (int)(Math.abs((this.getLoc().getX()-other.getLoc().getX())));
		int y = (int)(Math.abs(this.getLoc().getY()-other.getLoc().getY()));
		
		return (int)(Math.sqrt(x*x+y*y));
	}
	public int getDistance(Point p){
		int x = (int) Math.abs(this.getLoc().getX()-p.x);
		int y = (int) Math.abs(this.getLoc().getY()-p.y);
		return (int)(Math.sqrt(x*x+y*y));
	}
	public int getDistanceX(Point p){
		return (int) Math.abs(this.getLoc().getX()-p.x);
	}
	public int getDistanceY(Point p){
		return (int) Math.abs(this.getLoc().getY()-p.y);
	}
	public int getDistanceX(Boid other){
		return (int)(this.getLoc().getX()-other.getLoc().getX());
		
	}
	public int getDistanceY(Boid other){
		return (int) (this.getLoc().getY()-other.getLoc().getY());
	}
	public void move(){
		loc.setLocation(loc.getX()+vel.getX(), loc.getY()+vel.getY());
	}
	public void changeVelX(double dir){
		lastVel.setLocation(vel.getX(), vel.getY());
		if (((vel.getX() > -1*TERM_VEL) && (vel.getX() < TERM_VEL)) || true){
			if (dir < 0)// || (vel.getX() < 0 || scalerVel(vel) > 10))
				vel.setLocation(vel.getX()-acc, vel.getY());
			if (dir > 0)// || (vel.getX() > 0 || scalerVel(vel) > 10))
				vel.setLocation(vel.getX()+acc, vel.getY());
		}
	}
	public void changeVelY(double dir){
		lastVel.setLocation(vel.getX(), vel.getY());
		if ((vel.getY() > -1*TERM_VEL && vel.getY() < TERM_VEL) || true){
			if (dir < 0)// || (vel.getY() < 0 || scalerVel(vel) > 10))
				vel.setLocation(vel.getX(), vel.getY()-acc);
			if (dir > 0)// || (vel.getY() < 0 || scalerVel(vel) > 10))
				vel.setLocation(vel.getX(), vel.getY()+acc);
		}
	}
	public void changeVel(double dirX, double dirY, int distX, int distY){
		lastVel.setLocation(vel.getX(), vel.getY());
		if (((vel.getX() < TERM_VEL && vel.getX() > -1*TERM_VEL) && ((vel.getY() < TERM_VEL && vel.getY() > -1*TERM_VEL))) || true){
			double sX = 0, sY = 0; //signs of x&y
			double factorX = 2.0*Math.sqrt(distX)/10.0;
			double factorY = 2.0*Math.sqrt(distY)/10.0;
			if (dirX < 0 && (vel.getX() < 0 || scalerVel(vel) > MIN_VEL) && (vel.getX() > -1*TERM_VEL))
				sX = -1;
			if (dirX > 0 && (vel.getX() > 0 || scalerVel(vel) > MIN_VEL) && (vel.getX() < TERM_VEL))
				sX = 1;
			if (dirY < 0 && (vel.getY() < 0 || scalerVel(vel) > MIN_VEL) && (vel.getY() > -1*TERM_VEL))
				sY =-1;
			if (dirY > 0 && (vel.getY() > 0 || scalerVel(vel) > MIN_VEL) && (vel.getY() <TERM_VEL))
				sY = 1;
			factorX*=sX;
			factorY*=sY;
			vel.setLocation(vel.getX()+factorX*acc, vel.getY()+factorY*acc);
			
//			System.out.println(scalerVel(vel) + "   " + vel);
			if (Math.abs(vel.getX()) > TERM_VEL || Math.abs(vel.getY()) > TERM_VEL || Math.abs(vel.getX()) < MIN_VEL || Math.abs(vel.getY()) < MIN_VEL)
				System.err.println(vel);
		}
	}
	public void changeDirX(){
		vel.setLocation(vel.getX()*-1, vel.getY());
	}
	public void changeDirY(){
		vel.setLocation(vel.getX(), vel.getY()*-1);
	}
	public static Point2D addVectors(Point2D p1, Point2D p2){
		double x = p1.getX()+p2.getX();
		double y = p1.getY()+p2.getY();
		Point2D pf = new Point((int)x, (int)y);
		return pf;
	}
	public static Point2D subVectors(Point2D p1, Point2D p2){
		double x = p1.getX()-p2.getX();
		double y = p1.getY()-p2.getY();
		Point2D pf = new Point((int)x, (int)y);
		return pf;
	}
	public static double scalerVel(double x, double y){
		return (Math.sqrt((x*x)+(y*y)));
	}
	public static double scalerVel(Point2D v){
		return scalerVel(v.getX(), v.getY());
	}

	
}
