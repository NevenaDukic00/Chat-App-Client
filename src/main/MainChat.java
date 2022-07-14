package main;

import javafx.application.Application;
import javafx.stage.Stage;
import layout.Layout;

public class MainChat extends Application{

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		new Layout(primaryStage);
	}
}
