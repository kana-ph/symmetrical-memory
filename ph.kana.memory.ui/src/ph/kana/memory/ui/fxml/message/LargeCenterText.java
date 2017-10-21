package ph.kana.memory.ui.fxml.message;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import ph.kana.memory.ui.fxml.UiCommons;

public class LargeCenterText extends TilePane {

	@FXML private Label messageLabel;

	public LargeCenterText() {
		UiCommons.loadFxmlFile(this, "large-center-text.fxml");
	}

	public void setMessage(String message) {
		messageLabel.setText(message);
	}

	public String getMessage() {
		return messageLabel.getText();
	}
}
