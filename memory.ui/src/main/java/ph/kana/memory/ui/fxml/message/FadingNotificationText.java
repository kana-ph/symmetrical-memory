package ph.kana.memory.ui.fxml.message;

import javafx.animation.FadeTransition;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.util.Duration;
import ph.kana.memory.ui.fxml.UiCommons;

import java.util.function.Consumer;

public class FadingNotificationText extends TilePane {

	@FXML private Label messageLabel;

	private final Consumer<FadingNotificationText> cleanup;

	public FadingNotificationText(Consumer<FadingNotificationText> cleanup) {
		UiCommons.loadFxmlFile(this, "fading-notification-text.fxml");
		this.cleanup = cleanup;

		messageLabel.setOnMouseClicked(this::executeCleanup);
	}

	public void showText(String text) {
		messageLabel.setText(text);

		setOpacity(1.0);
		FadeTransition fadeTransition = new FadeTransition(Duration.millis(200.0), this);
		fadeTransition.setDelay(Duration.millis(4000.0));
		fadeTransition.setFromValue(1.0);
		fadeTransition.setToValue(0.0);
		fadeTransition.setCycleCount(1);
		fadeTransition.setAutoReverse(false);

		fadeTransition.setOnFinished(this::executeCleanup);
		fadeTransition.play();
	}

	private void executeCleanup(Event event) {
		cleanup.accept(this);
	}
}
