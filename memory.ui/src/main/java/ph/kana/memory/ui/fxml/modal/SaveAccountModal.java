package ph.kana.memory.ui.fxml.modal;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ph.kana.memory.account.AccountService;
import ph.kana.memory.account.CorruptDataException;
import ph.kana.memory.model.Account;
import ph.kana.memory.stash.StashException;
import ph.kana.memory.ui.fxml.UiCommons;

import java.util.Objects;

public class SaveAccountModal extends AbstractTilePaneModal<Account, Account> {

	@FXML private Label titleLabel;
	@FXML private TextField domainTextBox;
	@FXML private TextField usernameTextBox;
	@FXML private TextField unmaskedPasswordTextBox;
	@FXML private PasswordField maskedPasswordTextBox;
	@FXML private CheckBox maskPasswordToggle;

	private final AccountService accountService = AccountService.INSTANCE;
	private Account account;

	public SaveAccountModal() {
		super("save-account-modal.fxml");

		UiCommons.bindPasswordToggle(maskedPasswordTextBox, unmaskedPasswordTextBox, maskPasswordToggle);
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
		var accountId = (Objects.nonNull(account))? account.getId(): null;
		var domain = domainTextBox.getText();
		var username = usernameTextBox.getText();
		var rawPassword = maskedPasswordTextBox.getText();
		if (domain.isEmpty() || username.isEmpty() || rawPassword.isEmpty()) {
			showBottomFadingText("All fields required!");
			return;
		}
		try {
			var account = (Objects.nonNull(this.account))? this.account: new Account();
			account.setId(accountId);
			account.setDomain(domain);
			account.setUsername(username);
			account = accountService.saveAccount(account, rawPassword);
			setResult(account);

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

	private void initializeFields(Account account) {
		titleLabel.setText("Update Account");
		domainTextBox.setText(account.getDomain());
		usernameTextBox.setText(account.getUsername());
	}
}
