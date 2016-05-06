package Boids2D;

import java.awt.Container;

import javax.swing.JFrame;

public class Main extends JFrame{
	public static void main(String[] args){
		JFrame frame = new JFrame("Boids");
		Container window;
		frame.setVisible(true);
		frame.setSize(700,700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window = frame.getContentPane();
		window.add(new Boids_Panel(100, 2));
	}
}
