package ph.kana.memory.ui.fxml.modal;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import ph.kana.memory.model.PinStatus;
import ph.kana.memory.stash.AuthService;
import ph.kana.memory.stash.StashException;

import static ph.kana.memory.ui.fxml.UiCommons.forceNumericalInput;

public class LoginModal extends AbstractTilePaneModal<PinStatus> {

	@FXML private Label firstTimeLabel;
	@FXML private TextField pinTextBox;

	private final AuthService authService = AuthService.getInstance();

	public LoginModal() {
		super("login-modal.fxml");
		forceNumericalInput(pinTextBox);
	}

	@Override
	public void showModal(PinStatus pinStatus) {
		if (PinStatus.NEW == pinStatus) {
			String message = String.format("Initial PIN Code: %s", AuthService.DEFAULT_PIN);
			firstTimeLabel.setText(message);
		}
		setVisible(true);
	}

	@FXML
	public void validatePin() {
		var pin = pinTextBox.getText();
		if (pin.isEmpty()) {
			return;
		}
		try {
			if (authService.checkValidPin(pin)) {
				setVisible(false);
			} else {
				pinTextBox.setText("");
			}
		} catch (StashException e) {
			showBottomFadingText("Cannot validate PIN");
			e.printStackTrace(System.err);
		}
	}
}
