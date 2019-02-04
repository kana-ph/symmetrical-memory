package ph.kana.memory.ui.fxml.modal;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import ph.kana.memory.auth.AuthService;
import ph.kana.memory.stash.StashException;
import ph.kana.memory.type.LoginFlag;

import static ph.kana.memory.ui.fxml.UiCommons.forceNumericalInput;

public class LoginModal extends AbstractTilePaneModal<LoginFlag, Void> {

	@FXML private Label subMessageLabel;
	@FXML private TextField pinTextBox;

	private final AuthService authService = AuthService.INSTANCE;

	public LoginModal() {
		super("login-modal.fxml");
		forceNumericalInput(pinTextBox);
	}

	@Override
	public void showModal(LoginFlag pinStatus) {
		if (LoginFlag.FIRST_TIME == pinStatus) {
			String message = String.format("Initial PIN Code: %s", authService.fetchDefaultPin());
			subMessageLabel.setText(message);
		} else if (LoginFlag.SESSION_EXPIRE == pinStatus) {
			subMessageLabel.setText("Logged out due to inactivity.");
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
				close();
			} else {
				pinTextBox.setText("");
			}
		} catch (StashException e) {
			showBottomFadingText("Cannot validate PIN");
			e.printStackTrace(System.err);
		}
	}
}
