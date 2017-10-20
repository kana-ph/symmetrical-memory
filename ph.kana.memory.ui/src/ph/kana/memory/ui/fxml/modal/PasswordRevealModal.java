package ph.kana.memory.ui.fxml.modal;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;

import java.io.IOException;

public class PasswordRevealModal extends AbstractTilePaneModal {

	@FXML private TextField passwordRevealTextBox;

	private String password; // TODO change to char array

	public PasswordRevealModal() {
		super("password-reveal-modal.fxml");
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

	public void showModal(String password) {
		this.password = password;
		setVisible(true);
	}
}
