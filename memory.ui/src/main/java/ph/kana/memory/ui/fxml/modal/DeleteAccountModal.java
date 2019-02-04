package ph.kana.memory.ui.fxml.modal;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import ph.kana.memory.account.AccountService;
import ph.kana.memory.account.CorruptDataException;
import ph.kana.memory.model.Account;
import ph.kana.memory.stash.StashException;

public class DeleteAccountModal extends AbstractTilePaneModal<Account, Boolean> {

	@FXML private Text usernameText;
	@FXML private Text domainText;

	private final AccountService accountService = AccountService.INSTANCE;
	private Account account;

	public DeleteAccountModal() {
		super("delete-account-modal.fxml");
		setResult(false);
	}

	@Override
	public void showModal(Account account) {
		this.account = account;
		usernameText.setText(account.getUsername());
		domainText.setText(account.getDomain());
		setVisible(true);
	}

	@FXML
	public void deleteAccount() {
		try {
			accountService.deleteAccount(account);
			setResult(true);

			showBottomFadingText("Deletion success!");
		} catch (StashException e) {
			showBottomFadingText("Delete failed!");
			e.printStackTrace(System.err);
		} catch (CorruptDataException e) {
			handleCorruptDb(e);
		} finally {
			close();
		}
	}
}
