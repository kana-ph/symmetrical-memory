package ph.kana.memory.ui.fxml.modal;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ph.kana.memory.model.Account;
import ph.kana.memory.stash.AccountService;
import ph.kana.memory.stash.CorruptDataException;
import ph.kana.memory.stash.StashException;

import java.util.Objects;

public class SaveAccountModal extends AbstractTilePaneModal<Account> {

	@FXML private Label titleLabel;
	@FXML private TextField domainTextBox;
	@FXML private TextField usernameTextBox;
	@FXML private TextField unmaskedPasswordTextBox;
	@FXML private PasswordField maskedPasswordTextBox;
	@FXML private CheckBox maskPasswordToggle;

	private final AccountService accountService = AccountService.getInstance();
	private Account account;

	public SaveAccountModal() {
		super("save-account-modal.fxml");

		bindPasswordToggle();
	}

	@Override
	public void showModal(Account account) {
		this.account = account;

		if (Objects.isNull(account)) {
			titleLabel.setText("Add Account");
		} else {
			initializeFields(account);
		}
		maskedPasswordTextBox.setText("");
		setVisible(true);
	}

	@FXML
	public void saveAccount() {
		String accountId = (null != account)? account.getId(): null;
		String domain = domainTextBox.getText();
		String username = usernameTextBox.getText();
		String rawPassword = maskedPasswordTextBox.getText();
		if (domain.isEmpty() || username.isEmpty() || rawPassword.isEmpty()) {
			showBottomFadingText("All fields required!");
			return;
		}
		try {
			accountService.saveAccount(accountId, domain, username, rawPassword);

			showBottomFadingText("Saving success!");
			close();
		} catch (StashException e) {
			showBottomFadingText("Saving failed!");
			e.printStackTrace(System.err);
			close();
		} catch (CorruptDataException e) {
			handleCorruptDb(e);
		} finally {
			domainTextBox.setText("");
			usernameTextBox.setText("");
			maskedPasswordTextBox.setText("");
		}
	}

	private void bindPasswordToggle() {
		unmaskedPasswordTextBox.textProperty()
				.bindBidirectional(maskedPasswordTextBox.textProperty());
		maskedPasswordTextBox.visibleProperty()
				.bind(maskPasswordToggle.selectedProperty().not());
	}

	private void initializeFields(Account account) {
		titleLabel.setText("Update Account");
		domainTextBox.setText(account.getDomain());
		usernameTextBox.setText(account.getUsername());
	}
}
