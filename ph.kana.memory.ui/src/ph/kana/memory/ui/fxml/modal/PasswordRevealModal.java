package ph.kana.memory.ui.fxml.modal;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import ph.kana.memory.model.Account;
import ph.kana.memory.stash.PasswordService;
import ph.kana.memory.stash.StashException;

import java.util.Arrays;

public class PasswordRevealModal extends AbstractTilePaneModal<Account, Void> {

	@FXML private TextField passwordRevealTextBox;

	private final PasswordService passwordService = PasswordService.getInstance();
	private byte[] password;

	public PasswordRevealModal() {
		super("password-reveal-modal.fxml");
	}

	@Override
	public void showModal(Account account) {
		try {
			this.password = passwordService.fetchClearPassword(account);
			setVisible(true);
		} catch (StashException e) {
			close();
			showBottomFadingText("Failed retrieve password.");
			// TODO add logging
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

		showBottomFadingText("Password copied to clipboard!\nMake sure to clear clipboard after use.");
	}
}
