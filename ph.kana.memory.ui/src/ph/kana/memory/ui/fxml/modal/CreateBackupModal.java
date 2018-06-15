package ph.kana.memory.ui.fxml.modal;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Objects;

import static javafx.stage.FileChooser.ExtensionFilter;

public class CreateBackupModal extends AbstractTilePaneModal<Void, Void> {

	@FXML private Button saveButton;
	@FXML private TextField fileTextBox;
	private File backupFile = null;

	private static String INITIAL_FILE = System.getProperty("user.home") +
			System.getProperty("file.separator") +
			"Documents" +
			System.getProperty("file.separator") +
			"pwd-locker.bak";
	private static ExtensionFilter FILE_FILTER = new ExtensionFilter("Backup File (*.bak)","*.bak");

	public CreateBackupModal() {
		super("create-backup-modal.fxml");
	}

	@FXML
	public void openFileChooser() {
		var fileChooser = new FileChooser();
		fileChooser.setTitle("Save Backup as...");
		fileChooser.setInitialFileName(INITIAL_FILE);
		fileChooser.getExtensionFilters()
				.add(FILE_FILTER);
		fileChooser.setSelectedExtensionFilter(FILE_FILTER);

		var window = getScene().getWindow();
		backupFile = fileChooser.showSaveDialog(window);

		if (Objects.nonNull(backupFile)) {
			var filename = backupFile.getAbsolutePath();
			fileTextBox.setText(filename);
			fileTextBox.positionCaret(filename.length());

			Platform.runLater(() ->  saveButton.setDisable(false));
		}
	}

	@FXML
	public void createBackup() {
		showBottomFadingText("Successfully created backup:\n" + backupFile.getAbsolutePath());
		close();
	}

	@Override
	public void showModal(Void data) {
		setVisible(true);
	}
}
