package ph.kana.memory.ui.fxml.modal;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;

import java.io.IOException;

public class PasswordRevealModal extends TilePane {

	@FXML private TextField passwordRevealTextBox;

	private String password; // TODO change to char array

	public PasswordRevealModal() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("password-reveal-modal.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(1);
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

	public void showModal(String password) {
		this.password = password;
		setVisible(true);
	}
}
