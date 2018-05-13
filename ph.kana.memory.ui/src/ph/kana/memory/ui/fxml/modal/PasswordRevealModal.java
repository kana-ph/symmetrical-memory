package ph.kana.memory.ui.fxml.modal;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import ph.kana.memory.model.Account;
import ph.kana.memory.stash.AuthService;
import ph.kana.memory.stash.StashException;

import java.util.Arrays;

public class PasswordRevealModal extends AbstractTilePaneModal<Account> {

	@FXML private TextField passwordRevealTextBox;

	private final AuthService authService = AuthService.getInstance();
	private byte[] password;

	public PasswordRevealModal() {
		super("password-reveal-modal.fxml");
	}

	@Override
	public void showModal(Account account) {
		String encryptedPassword = account.getPasswordFile();
		String timestamp = Long.toString(account.getSaveTimestamp());
		try {
			this.password = authService.decryptPassword(encryptedPassword, timestamp);
			setVisible(true);
		} catch (StashException e) {
			close();
			showBottomFadingText("Something went wrong.");
		}
	}

	@FXML @Override
	public void close() {
		Arrays.fill(password, (byte) 0);
		super.close();
	}

	@FXML
	public void showPassword() {
		passwordRevealTextBox.setText(new String(password));
	}

	@FXML
	public void hidePassword() {
		passwordRevealTextBox.setText("");
	}

	@FXML
	public void copyPassword() {
		ClipboardContent clipboardContent = new ClipboardContent();
		clipboardContent.putString(new String(password));

		Clipboard.getSystemClipboard()
				.setContent(clipboardContent);
	}
}
