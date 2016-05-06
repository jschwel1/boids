package Boids2D;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;

public class Boids_Panel extends JPanel implements ActionListener{
	//=============== INSTANCE FIELDS ===================//
//	public static final int PERSONAL_SPACE = 150;
	public double ZOOM = 5;
	ArrayList<Boid> boids;
	ArrayList<Boid> predators;
	long t1;
	long tWind;
	boolean wind;	
	Timer clock;
	Point avg = new Point(0,0);
	Point pAvg = new Point(0,0);
	JSlider numBoids, numPreds, zoomSlide;
	JFrame options;
	JButton reset;
	// =============== CONSTRUCTORS ======================//
	public Boids_Panel(){
		this(5,1);
	}
	public Boids_Panel(int n){
		this(n, 2);
	}
	public Boids_Panel(int n, int p){
		boids = new ArrayList<Boid>();
		for (int i = 0; i < n; i++){
			int p1 = (int)(Math.random()*500*ZOOM);
			int p2 = (int)(Math.random()*500*ZOOM);
			boids.add(new Boid(new Point(p1, p2)));
		}
		predators = new ArrayList<Boid>();
		for(int i = 0; i < p; i++){
			int p1 = (int)(Math.random()*500*ZOOM);
			int p2 = (int)(Math.random()*500*ZOOM);
			predators.add(new Boid(new Point(p1, p2)));
			predators.get(i).setAcc(Math.random()*2+1);
			predators.get(i).setPersonalSpace(450);
		}

		clock = new Timer(50, this);
		clock.start();
		clock.setActionCommand("clock");
		t1 = System.currentTimeMillis();
		wind=false;
		// options frame
		numBoids = new JSlider(JSlider.HORIZONTAL, 2,1000,100);
		numBoids.setMajorTickSpacing(100);
		numBoids.setPaintTicks(true);
		numPreds = new JSlider(JSlider.HORIZONTAL, 0, 10, 2);
		numPreds.setPaintTicks(true);
		numPreds.setMajorTickSpacing(1);
		zoomSlide = new JSlider(JSlider.HORIZONTAL, 1, 100, 30);
		zoomSlide.setMajorTickSpacing(10);
		zoomSlide.setPaintTicks(true);
		reset = new JButton("Reset");
		reset.setActionCommand("Reset");
		reset.addActionListener(this);
		options = new JFrame("options");
		options.setVisible(true);
		options.setLocation(750, 0);
		options.setLayout(new GridLayout(4,1));
		options.setSize(300,300);
		options.add(numBoids);
		options.add(numPreds);
		options.add(zoomSlide);
		options.add(reset);
		repaint();
		this.revalidate();
	}
	public void reset(int n, int p){
		boids = new ArrayList<Boid>();
		for (int i = 0; i < n; i++){
			int p1 = (int)(Math.random()*500*ZOOM);
			int p2 = (int)(Math.random()*500*ZOOM);
			boids.add(new Boid(new Point(p1, p2)));
		}
		predators = new ArrayList<Boid>();
		for(int i = 0; i < p; i++){
			int p1 = (int)(Math.random()*700*ZOOM);
			int p2 = (int)(Math.random()*700*ZOOM);
			predators.add(new Boid(new Point(p1, p2)));
			predators.get(i).setAcc(2);
			predators.get(i).setPersonalSpace(550);
		}
		wind=false;
		this.revalidate();
		this.repaint();
	}

	// ================ METHODS =========================//
	// fly towards center of mass
	public void rule1(int index, ArrayList<Boid> boid){
		double avgX = 350/ZOOM;
		double avgY = 350/ZOOM;
		pAvg = new Point(avg);
		// find center of all other boids
		for (int i = 0; i < boid.size(); i++){
			if (i != index){
				avgX+=boid.get(i).getLoc().getX();
				avgY+=boid.get(i).getLoc().getY();
				if (i <= boid.size()/4){
					pAvg.x+=boid.get(i).getLoc().getX();
					pAvg.y+=boid.get(i).getLoc().getY();
				}
			}
		}
		if (boid.size() > 0){
			pAvg.x/=((boid.size())/4);
			pAvg.y/=((boid.size())/4);
		}
		if (wind){ // simulate extra birds in the center to draw some attention there
			for (int i = 0; i < .4*boid.size(); i++){
				avgX+=350*ZOOM;
				avgY+=350*ZOOM;
			}
			avgX/=(boid.size()+.3*boid.size()-1);
			avgY/=(boid.size()+.3*boid.size()-1);
		}
		else{
			avgX/=(boid.size()-1);
			avgY/=(boid.size()-1);
		}
		//		System.out.print(avgX+ ", " + avgY + "  \t " + boid.get(index).getLoc().getX() + ", " +boid.get(index).getLoc().getY());

		if (avgX > 650*ZOOM)
			avgX = 350*ZOOM;
		if (avgX < 50)
			avgX = 350;
		if (avgY > 650*ZOOM)
			avgY = 350*ZOOM;
		if (avgY < 50)
			avgY = 350;

		boid.get(index).changeVel(avgX-boid.get(index).getLoc().getX(), avgY-boid.get(index).getLoc().getY(), boid.get(index).getDistanceX(new Point((int)avgX,(int)avgY)), boid.get(index).getDistanceY(new Point((int)avgX,(int)avgY)));
		

	}

	// keep distance away from other boids
	public void rule2(int index, ArrayList<Boid> boid){
		// get distances from other boids
		//		Point dist = new Point(0,0);
		for (int i = 0; i < boid.size(); i++){
			if (i != index){
				if (boid.get(index).getDistance(boid.get(i)) < boid.get(index).getPersonalSpace()){
					boid.get(index).changeVelX(boid.get(index).getDistanceX(boid.get(i)));
					boid.get(index).changeVelY(boid.get(index).getDistanceY(boid.get(i)));
				}
			}
		}
	}

	// match velocity of near boids
	public void rule3(int index, ArrayList<Boid> boid){
		double avgX = 0;
		double avgY = 0;
		
		for (int i = 0; i < boid.size(); i++){
			if (i != index){
				if (boid.get(index).getDistance(boid.get(i)) < boid.get(index).getPersonalSpace()*2){ // within x times personal space radius
					avgX+=boid.get(i).getVel().getX();
					avgY+=boid.get(i).getVel().getY();
				}
			}
		}
		avgX/=(boid.size()-1);
		avgY/=(boid.size()-1);
		if (boid.get(index).getVel().getX() > avgX){ // faster than nearby boids
			boid.get(index).changeVelX(-1);
			//				System.out.print("a");
		}
		else if (boid.get(index).getVel().getX() < avgX){ // slower than nearby boids
			boid.get(index).changeVelX(1);
			//				System.out.print("b");
		}
		if (boid.get(index).getVel().getY() > avgY){ // faster than nearby boids
			boid.get(index).changeVelY(-1);
			//				System.out.print("c");
		}
		else if (boid.get(index).getVel().getY() < avgY){ // slower than nearby boids
			boid.get(index).changeVelY(1);
			//				System.out.print("d");
		}
		//			System.out.println(" - avg: (" + avgX + ", " + avgY+") " + index + ": (" + boids.get(index).getVel().getX() + ", " + boids.get(index).getVel().getY() + ")");


	}
	// keep birds within bounds
	/*
	 * this method has been implemented in rule1 and is no longer in use
	 */
	public void rule4(){
		for(Boid b: predators){
			if (b.getLoc().getX() < 100){
				b.changeVelX(1);
			}
			if (b.getLoc().getX() > 600*ZOOM){
				b.changeVelX(-1);
			}
			if (b.getLoc().getY() < 100){
				b.changeVelY(1);
			}
			if (b.getLoc().getY() > 600*ZOOM){
				b.changeVelY(-1);
			}

		}
	}

	// stay away from predators (similar to rule2, but move faster)
	public void rule5(int index){
		for (Boid p: predators){
			if (boids.get(index).getDistance(p) < boids.get(index).getPersonalSpace()*3){
				for (int c = 0; c < 3; c++){
					boids.get(index).changeVelX(boids.get(index).getLoc().getX() - p.getLoc().getX());
					boids.get(index).changeVelY(boids.get(index).getLoc().getY() - p.getLoc().getY());
				}
			}
		}
	}
	public void pRule1(ArrayList<Boid> pred, ArrayList<Boid> boid){
		for (Boid p: pred){
			p.changeVelX(pAvg.x-p.getLoc().getX());
			p.changeVelY(pAvg.y-p.getLoc().getY());
			// eat nearby boids
			for (int b= 0; b< boid.size(); b++){
				if (p.getDistance(boid.get(b)) < 65){// < eating radius
					boid.remove(b);
					b--;
				}
			}
		}
	}
	
	public void wind(int x, int y){
		for(Boid b: boids){
//			b.setVel((Point)Boid.addVectors(b.getVel(),new Point(x,y)));
		}
		
	}
	public void wind(){
		for(Boid b: boids){
			double slope = (avg.y - (350*ZOOM))/(avg.x - (350*ZOOM));
			if (avg.x < 350*ZOOM && slope < 0)
				slope*=-1;
			b.setVel((Point)Boid.addVectors(b.getVel(),new Point((int)(10*Math.cos(slope)),(int)(10*Math.sin(slope)))));
		}
		
	}
	// move all boids
	public void moveBoid(ArrayList<Boid> b){
		for (int i = 0; i < b.size(); i++){
			rule1(i, b);
			rule2(i, b);
			rule3(i, b);
			rule5(i);
			//			System.out.println();
			b.get(i).move();
		}
		
		repaint();
	}
	public void movePred(ArrayList<Boid> p, ArrayList<Boid> b){
		for (int i = 0; i < p.size(); i++){
			pRule1(p, b);
			rule2(i, p);
			rule3(i, p);
			rule4();
			//			System.out.println();
			p.get(i).move();
		}
		repaint();
	}
	// ================= PAINT ======= //
	public void paintComponent(Graphics g){
		Graphics2D g2 =(Graphics2D)g;
		g2.setStroke(new BasicStroke((int)Math.ceil(10.0/ZOOM)));
		g.setColor(new Color(200,200,250));
		g.fillRect(0,0,800,800);
		g.setColor(Color.black);
		for (Boid b: boids){
//			g.fillOval((int)(b.getLoc().getX()/ZOOM), (int)(b.getLoc().getY()/ZOOM), (int)Math.ceil((10/ZOOM)), (int)(Math.ceil((10/ZOOM))));
//			g.drawLine((int)(b.getLoc().getX()/ZOOM), (int)(b.getLoc().getY()/ZOOM), (int)((b.getLoc().getX()-b.getVel().getX())/ZOOM), (int)((b.getLoc().getY() - b.getVel().getY())/ZOOM));
			drawTriangle(g2, b.getLoc(), b.getVel(), 20/ZOOM);
			
		}
		g.setColor(new Color(200,50,50));
		for (Boid b: predators){
//			g.fillOval((int)(b.getLoc().getX()/ZOOM), (int)(b.getLoc().getY()/ZOOM), (int)(Math.ceil((30/ZOOM))), (int)(Math.ceil((30/ZOOM))));
			drawTriangle(g2, b.getLoc(), b.getVel(), 30/ZOOM, 5);
		}
//		g.setColor(Color.blue);
//		if (wind)
//			g.drawLine((int)(avg.x/ZOOM), (int)(avg.y/ZOOM), 350, 350);
		// paint the average location -v 
		/*
		g.setColor(Color.red);
		g.fillOval((int)(avg.x/ZOOM), (int)(avg.y/ZOOM), (int)(5/1), (int)(5/1));
		 */
		
		// FPS
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
		g.setColor(Color.gray);
		int fps = (int)(1.0/((double)(System.currentTimeMillis()-t1)/1000.0));
		t1= System.currentTimeMillis();
		g.drawString("FPS: " + fps, 0, 650);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("clock")){
			avg.x = 0;
			avg.y = 0;
			for (Boid b: boids){
				avg.x += b.getLoc().getX();
				avg.y += b.getLoc().getY();
			}
			//		System.out.print("  = " + avg.x);
			if (boids.size() > 0){
				avg.x/=boids.size();
				avg.y/=boids.size();
			}
			moveBoid(boids);
			movePred(predators, boids);
			ZOOM=zoomSlide.getValue()/10;
			if (!wind){
				if(Math.random()*1000 <= 10){
					wind = true;
					tWind = System.currentTimeMillis();
				}
			}
			else {
				if (System.currentTimeMillis()-tWind >= 2000)
					wind = false;
			}
			System.out.println(wind+ " boids.size() = "+boids.size());
			if (predators.size() < numPreds.getValue()){
				
					int p1 = (int)(Math.random()*700*ZOOM);
					int p2 = (int)(Math.random()*700*ZOOM);
					predators.add(new Boid(new Point(p1, p2)));
					predators.get(predators.size()-1).setAcc(1);
					predators.get(predators.size()-1).setPersonalSpace(450);
			}
			else if (predators.size() > numPreds.getValue()){
				predators.remove(predators.size()-1);
			}
				
		}
		
		if (e.getActionCommand().equals("Reset")){			
			reset(numBoids.getValue(), numPreds.getValue());
		}
	}
	/**
	 * draw triangle: the idea is to use the current velocity to draw a triangle in the correct direction for the boid.
	 * note: the main vertex is the current location, the other two are found in here.
	 * @param g - the graphics jawn
	 * @param loc - location of the boid
	 * @param vel - velocity of the boid
	 * @param rad - radius/ height of the triangle
	 */
	public void drawTriangle(Graphics2D g, Point2D loc, Point2D vel, double rad, int width){
		
		// first off, i'd like to apologize for the disgustingly shitty code. i'll do my best to explain
		// everything that's going on...
		/*
		 * to start off, i should share a few equations that i used:
		 * 1)	y1-y = m(x1-x) -- x1/y1 are the unknown coordinates & x/y are the location coordinates
		 * 		y1 = m(x1 - x) + y <--just moved the y over
		 * 2)	d^2 = (x-x1)^2 + (y-y1)^2  <-- distance formula
		 * 		x1 = x- sqrt(d^2/(m^2+1)) <-- a bit of derivations (had to mix in eq1)
		 *  
		 */
		double x = loc.getX(); // current location
		double y = loc.getY(); // -^
		double m, mp; // slope and perpendicular slope
		double x1, y1; // altitude line coords (along with x,y) 
		double vx1, vy1, vx2, vy2; // vetices for the triangle
		double r2 = width*rad; // for the triangle
		int[] xPoints, yPoints; // arrays for the fillPolygon function to work easier
		xPoints = new int[4];
		yPoints = new int[4];
		
		// get the slope
		if (vel.getX() == 0){ // if the x-velocity is zero, rather than getting an error, 
			m = vel.getY()/.0001; // divide by a really small num
			if (vel.getY() < 0)
				m*=-1;
		}
		else
			 m = vel.getY()/vel.getX(); // otherwise, get the slope of the velocity
		// one step at a time, get x1 ( basically the alititude of the triangle)
		x1 = (rad*rad);	
		x1/=Math.sqrt(((m*m)+1));
		// look for negatives to see if the x should be before or after the current location
		// because the equation has a sqrt, it will only return positives, so this is the double check.
		if (vel.getX() > 0)
			x1= x-x1;
		else
			x1= x+x1;
		// find y1
		if (vel.getY() != 0)
			y1 = m*(x1 - x) + y;
		else 
			y1 = y;
		// perpendicular slope (the if/if_else/else helps make the triangles disappear less when the one of
		// the velocities is 0. b/c the slope fixed slopes are either 0 or very big, +/- has a negligable 
		// difference
		if (vel.getX() == 0)
			mp = 0;
		else if (vel.getY() == 0)
			mp = 99999999;
		else
			mp = -1/m;
		
		// same equations as x1/y1, but for the other corners of the triangle
		vx1 = x1 - Math.sqrt((r2*r2)/((mp*mp)+1));
		vy1 = (mp*(vx1-x1))+y1;
		vx2 = x1 + Math.sqrt((r2*r2)/((mp*mp)+1));
		vy2 = (mp*(vx2-x1))+y1;
		
		// because i'm dumb, i now have to put the found values into an array for xPoints and yPoints.
		// when you do this, i would reccomend not having x,y,vx1,vy1,vx2,vy2. do have a separate x1/y1 
		// though, because those are not needed in drawing of the triangle.
		// Map for the points (imagine one boid standing upright	/\
		// [0] -> the top vertex								   /  \
		// [1] -> the left side point							  /    \
		// [2] -> the the middle indent/^ looking piece			 /  /\  \
		// [3] -> the right side point							/__/  \__\	
		xPoints[0] = (int)(x/ZOOM);	
		xPoints[1] = (int)(vx1/ZOOM);
		xPoints[3] = (int)(vx2/ZOOM);
		xPoints[2] = (int)(((x+x1)/2)/ZOOM);
		yPoints[0] = (int)(y/ZOOM);
		yPoints[1] = (int)(vy1/ZOOM);
		yPoints[3] = (int)(vy2/ZOOM);
		yPoints[2] = (int)(((y+y1)/2)/ZOOM);
		// ignore most of these unless you want to see it... they were to help me see what was going wrong at first. 
		g.setStroke(new BasicStroke((int)Math.ceil(4.0/ZOOM)));
//		g.drawLine((int)(x/ZOOM), (int)(y/ZOOM), (int)(vx1/ZOOM), (int)(vy1/ZOOM));
//		g.drawLine((int)(x/ZOOM), (int)(y/ZOOM), (int)(vx2/ZOOM), (int)(vy2/ZOOM));
//		g.drawLine((int)(vx2/ZOOM), (int)(vy2/ZOOM), (int)(vx1/ZOOM), (int)(vy1/ZOOM));
		g.fillPolygon(xPoints, yPoints, 4); // <-- this is where the triangle is drawn.
	
		
		
	}
	public void drawTriangle(Graphics2D g, Point2D loc, Point2D vel, double rad){
		drawTriangle(g, loc, vel, rad, 3);
	}
}
