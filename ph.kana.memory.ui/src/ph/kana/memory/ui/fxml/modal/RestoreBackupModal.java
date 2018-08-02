package ph.kana.memory.ui.fxml.modal;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import ph.kana.memory.backup.BackupException;
import ph.kana.memory.backup.BackupService;
import ph.kana.memory.ui.fxml.UiCommons;

import java.io.File;
import java.util.Map;
import java.util.Objects;

import static javafx.stage.FileChooser.ExtensionFilter;

public class RestoreBackupModal extends AbstractTilePaneModal<Void, Void> {

	@FXML private Button saveButton;
	@FXML private TextField fileTextBox;
	@FXML private TextField unmaskedPasswordTextBox;
	@FXML private PasswordField maskedPasswordTextBox;
	@FXML private CheckBox maskPasswordToggle;
	private File backupFile = null;

	private final BackupService backupService = BackupService.getInstance();

	private static ExtensionFilter FILE_FILTER = new ExtensionFilter("Backup File (*.bak)","*.bak");

	public RestoreBackupModal() {
		super("restore-backup-modal.fxml");

		UiCommons.bindShowablePasswordField(maskedPasswordTextBox, unmaskedPasswordTextBox, maskPasswordToggle);
	}

	@FXML
	public void openFileChooser() {
		var fileChooser = new FileChooser();
		fileChooser.setTitle("Open Backup");
		fileChooser.getExtensionFilters()
				.add(FILE_FILTER);
		fileChooser.setSelectedExtensionFilter(FILE_FILTER);

		var window = getScene().getWindow();
		backupFile = fileChooser.showOpenDialog(window);

		if (Objects.nonNull(backupFile)) {
			var filename = backupFile.getAbsolutePath();
			fileTextBox.setText(filename);
			fileTextBox.positionCaret(filename.length());
		}
	}

	@FXML
	public void restoreBackup() {
		var fieldValidateMap = Map.of(
				fileTextBox, "Please select backup file.",
				maskedPasswordTextBox, "Backup password is required!"
		);
		var formValid = validateFields(fieldValidateMap);
		if (formValid) {
			var password = maskedPasswordTextBox.getText();

			try {
				backupService.restoreBackup(backupFile, password);
				showBottomFadingText("Backup restored!");
				close();
			} catch (BackupException e) {
				e.printStackTrace(); // TODO use proper logging
				showBottomFadingText(e.getMessage());
			}
		}
	}

	@Override
	public void showModal(Void data) {
		setVisible(true);
	}
}
