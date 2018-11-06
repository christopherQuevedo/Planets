/* 
 * Author: Chris Quevedo
 * Course: CSC 422
 * Assignment: N-Body
 * Instructors: Patrick Homer
 * Due date: 11/7/2018
 * Program Language: Java 1.8
 *
 * PlanetGUI.java -- This program   
 */

import java.util.List;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class PlanetGUI extends Application {
	
	public static void main(String args[]) {
		launch(args);
	}
	
	private static BorderPane all;
	private static Canvas canvas;
	private static GraphicsContext gc;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		// make our 800 wide 500 tall canvas
		primaryStage.setTitle("PlanetsGUI");
		all = new BorderPane();
		canvas = new Canvas(800, 500);
		all.setTop(canvas);
		gc = canvas.getGraphicsContext2D();
		Scene scene = new Scene(all, 800, 500);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		// get our cmd line args
		List<String> args = getParameters().getRaw();
		
		// make a thread that handles moving the bodies
		// and give it the args and graphicscontext and start it
		MovePlanetsSequential in = new MovePlanetsSequential();
		in.setGC(gc);
		in.setArgs(args);
		in.start();
		
	}
	
}
