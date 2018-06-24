package ph.kana.memory.ui.fxml.modal;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

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

	private static ExtensionFilter FILE_FILTER = new ExtensionFilter("Backup File (*.bak)","*.bak");

	public CreateBackupModal() {
		super("create-backup-modal.fxml");

		bindPasswordToggle();
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
		if (!formValid) {
			return;
		}

		showBottomFadingText("Successfully created backup:\n" + backupFile.getAbsolutePath());
		close();
	}

	@Override
	public void showModal(Void data) {
		setVisible(true);
	}

	private void bindPasswordToggle() {
		unmaskedPasswordTextBox.textProperty()
				.bindBidirectional(maskedPasswordTextBox.textProperty());
		unmaskedPasswordTextBox.translateXProperty()
				.bindBidirectional((maskedPasswordTextBox.translateXProperty()));

		maskedPasswordTextBox.visibleProperty()
				.bind(maskPasswordToggle.selectedProperty().not());
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
