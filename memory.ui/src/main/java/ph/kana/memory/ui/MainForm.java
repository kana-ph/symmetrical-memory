package ph.kana.memory.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ph.kana.memory.ui.fxml.MainFormController;

public class MainForm extends Application {

	private static final double APP_WIDTH = 400;
	private static final double APP_HEIGHT = 595;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		var mainFormFxml = MainForm.class
			.getResource("/ph/kana/memory/ui/fxml/main-form.fxml");
		System.out.println("mainFormFxml = " + mainFormFxml);
		var loader = new FXMLLoader(mainFormFxml);
		var scene = new Scene(loader.load());

		setUserAgentStylesheet(STYLESHEET_MODENA);

		stage.setWidth(APP_WIDTH);
		stage.setMinWidth(APP_WIDTH);

		stage.setHeight(APP_HEIGHT);
		stage.setMinHeight(APP_HEIGHT);

		stage.setTitle("kana0011/password-locker");
		stage.setScene(scene);

		var controller = loader.<MainFormController>getController();
		controller.setHostServices(getHostServices());

		stage.show();
	}
}
