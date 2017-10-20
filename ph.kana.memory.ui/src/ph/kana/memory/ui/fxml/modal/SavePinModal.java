package ph.kana.memory.ui.fxml.modal;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ph.kana.memory.stash.AuthService;
import ph.kana.memory.stash.StashException;
import ph.kana.memory.ui.fxml.UiCommons;

import java.util.List;

public class SavePinModal extends AbstractTilePaneModal<Void> {

	@FXML private PasswordField currentPinTextBox;
	@FXML private TextField unmaskedPinTextBox;
	@FXML private PasswordField maskedPinTextBox;
	@FXML private CheckBox maskPinToggle;

	private final AuthService authService = AuthService.getInstance();

	public SavePinModal() {
		super("save-pin-modal.fxml");

		List.of(currentPinTextBox, unmaskedPinTextBox, maskedPinTextBox)
				.forEach(UiCommons::forceNumericalInput);
		bindPinToggle();
	}

	@Override
	public void showModal(Void data) {
		setVisible(true);
	}

	@FXML
	public void savePin() {
		String oldPin = currentPinTextBox.getText();
		String newPin = maskedPinTextBox.getText();

		try {
			if (oldPin.isEmpty() || !authService.checkValidPin(oldPin)) {
				showBottomFadingText("Current PIN is invalid!");
				return;
			}
			if (newPin.isEmpty()) {
				showBottomFadingText("New PIN is required!");
				return;
			}

			authService.saveClearPin(newPin);

			showBottomFadingText("PIN updated!");
			close();
		} catch (StashException e) {
			showBottomFadingText("Setting new PIN failed!");
			e.printStackTrace(System.err);
			close();
		}
	}

	private void bindPinToggle() {
		unmaskedPinTextBox.textProperty()
				.bindBidirectional(maskedPinTextBox.textProperty());
		maskedPinTextBox.visibleProperty()
				.bind(maskPinToggle.selectedProperty().not());
	}
}