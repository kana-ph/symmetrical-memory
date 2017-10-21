package ph.kana.memory.ui.fxml.modal;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import ph.kana.memory.model.Account;
import ph.kana.memory.stash.AuthService;
import ph.kana.memory.stash.StashException;

import java.io.IOException;

public class PasswordRevealModal extends AbstractTilePaneModal<Account> {

	@FXML private TextField passwordRevealTextBox;

	private AuthService authService = AuthService.getInstance();
	private String password; // TODO change to char array

	public PasswordRevealModal() {
		super("password-reveal-modal.fxml");
	}

	@Override
	public void showModal(Account account) {
		String encryptedPassword = account.getEncryptedPassword();
		String timestamp = Long.toString(account.getSaveTimestamp());
		try {
			this.password = authService.decryptPassword(encryptedPassword, timestamp);
			setVisible(true);
		} catch (StashException e) {
			close();
			showBottomFadingText("Something went wrong.");
		}
	}

	@FXML
	public void close() {
		password = "";
		setVisible(false);
	}

	@FXML
	public void showPassword() {
		passwordRevealTextBox.setText(password);
	}

	@FXML
	public void hidePassword() {
		passwordRevealTextBox.setText("");
	}
}
