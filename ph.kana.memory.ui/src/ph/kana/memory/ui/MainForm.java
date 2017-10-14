package ph.kana.memory.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainForm extends Application {

	private static final double APP_WIDTH = 350.0;
	private static final double APP_HEIGHT = 495.0;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/ph/kana/memory/ui/fxml/main-form.fxml"));
		Scene scene = new Scene(root);

		setUserAgentStylesheet(STYLESHEET_MODENA);

		stage.setWidth(APP_WIDTH);
		stage.setMinWidth(APP_WIDTH);

		stage.setHeight(APP_HEIGHT);
		stage.setMinHeight(APP_HEIGHT);

		stage.setTitle("kana0011/password-locker");
		stage.setScene(scene);

		stage.show();
	}
}
