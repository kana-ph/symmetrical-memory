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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

import static javafx.stage.FileChooser.ExtensionFilter;

public class CreateBackupModal extends AbstractTilePaneModal<Void, Void> {

	@FXML private Button saveButton;
	@FXML private TextField fileTextBox;
	@FXML private TextField unmaskedPasswordTextBox;
	@FXML private PasswordField maskedPasswordTextBox;
	@FXML private CheckBox maskPasswordToggle;
	private File backupFile = null;

	private final BackupService backupService = BackupService.INSTANCE;

	private static ExtensionFilter FILE_FILTER = new ExtensionFilter("Backup File (*.bak)","*.bak");

	public CreateBackupModal() {
		super("create-backup-modal.fxml");

		UiCommons.bindPasswordToggle(maskedPasswordTextBox, unmaskedPasswordTextBox, maskPasswordToggle);
	}

	@FXML
	public void openFileChooser() {
		var fileChooser = new FileChooser();
		fileChooser.setTitle("Save Backup as...");
		fileChooser.setInitialFileName(generateInitialFileName());
		fileChooser.getExtensionFilters()
				.add(FILE_FILTER);
		fileChooser.setSelectedExtensionFilter(FILE_FILTER);

		var window = getScene().getWindow();
		backupFile = fileChooser.showSaveDialog(window);

		if (Objects.nonNull(backupFile)) {
			var filename = backupFile.getAbsolutePath();
			fileTextBox.setText(filename);
			fileTextBox.positionCaret(filename.length());
		}
	}

	@FXML
	public void createBackup() {
		var fieldValidateMap = Map.of(
				fileTextBox, "Please select backup file.",
				maskedPasswordTextBox, "Backup password is required!"
		);
		var formValid = validateFields(fieldValidateMap);
		if (formValid) {
			var password = maskedPasswordTextBox.getText();

			try {
				var file = backupService.createBackup(backupFile, password);
				showBottomFadingText("Successfully created backup:\n" + file.getAbsolutePath());
				close();
			} catch (BackupException e) {
				e.printStackTrace();
				showBottomFadingText(e.getMessage());
			}
		}
	}

	@Override
	public void showModal(Void data) {
		setVisible(true);
	}

	private String generateInitialFileName() {
		var date = LocalDate.now();
		return System.getProperty("user.home") +
				System.getProperty("file.separator") +
				"Documents" +
				System.getProperty("file.separator") +
				"pwd-backup_" +
				date.format(DateTimeFormatter.ISO_DATE) +
				".bak";
	}
}
