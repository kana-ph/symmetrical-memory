package ph.kana.memory.ui.fxml.modal;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import ph.kana.memory.stash.AuthService;
import ph.kana.memory.stash.StashException;

public class LoginModal extends AbstractTilePaneModal {

	@FXML private TextField pinTextBox;

	private final AuthService authService = new AuthService();

	public LoginModal() {
		super("login-modal.fxml");
		forceNumericalInput(pinTextBox);
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
//			showBottomMessage("Cannot validate PIN"); // TODO: wait for it to be externalized
			e.printStackTrace(System.err);
		}
	}
}
