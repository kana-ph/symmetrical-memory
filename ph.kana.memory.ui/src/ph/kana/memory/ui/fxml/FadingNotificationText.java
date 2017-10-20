package ph.kana.memory.ui.fxml;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.util.Duration;

import java.io.IOException;
import java.util.function.Consumer;

public class FadingNotificationText extends TilePane {

	@FXML private Label messageLabel;

	public FadingNotificationText() {
		UiCommons.loadFxmlFile(this, "fading-notification-text.fxml");
	}

	public void showText(String text, Consumer<FadingNotificationText> cleanup) {
		messageLabel.setText(text);

		setOpacity(1.0);
		FadeTransition fadeTransition = new FadeTransition(Duration.millis(200.0), this);
		fadeTransition.setDelay(Duration.millis(3000.0));
		fadeTransition.setFromValue(1.0);
		fadeTransition.setToValue(0.0);
		fadeTransition.setCycleCount(1);
		fadeTransition.setAutoReverse(false);

		fadeTransition.setOnFinished(event -> cleanup.accept(this));
		fadeTransition.play();
	}
}
