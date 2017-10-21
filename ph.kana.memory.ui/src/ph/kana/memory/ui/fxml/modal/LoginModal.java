package ph.kana.memory.ui.fxml.modal;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import ph.kana.memory.stash.AuthService;
import ph.kana.memory.stash.StashException;
import ph.kana.memory.ui.fxml.UiCommons;

import static ph.kana.memory.ui.fxml.UiCommons.forceNumericalInput;
import static ph.kana.memory.ui.fxml.UiCommons.showBottomFadingText;

public class LoginModal extends AbstractTilePaneModal<Void> {

	@FXML private TextField pinTextBox;

	private final AuthService authService = AuthService.getInstance();

	public LoginModal() {
		super("login-modal.fxml");
		forceNumericalInput(pinTextBox);
	}

	@Override
	public void showModal(Void data) {
		setVisible(true);
	}

	@FXML
	public void validatePin() {
		String pin = pinTextBox.getText();
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
