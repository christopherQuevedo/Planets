/* 
 * Author: Chris Quevedo
 * Course: CSC 422
 * Assignment: N-Body
 * Instructors: Patrick Homer
 * Due date: 11/7/2018
 * Program Language: Java 1.8
 *
 * Body.java -- This program   
 */

public class Point {

	double x;
	double y;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	public void setY(double y) {
		this.y = y;
	}
}
