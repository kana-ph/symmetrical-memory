package ph.kana.memory.ui.fxml.modal;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import ph.kana.memory.model.Account;
import ph.kana.memory.stash.AccountService;
import ph.kana.memory.stash.CorruptDataException;
import ph.kana.memory.stash.StashException;
import ph.kana.memory.ui.fxml.UiCommons;

public class DeleteAccountModal extends AbstractTilePaneModal<Account> {

	@FXML private Text usernameText;
	@FXML private Text domainText;

	private final AccountService accountService = AccountService.getInstance();
	private Account account;

	public DeleteAccountModal() {
		super("delete-account-modal.fxml");
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

			UiCommons.showBottomFadingText("Deletion success!", getChildren());
		} catch (StashException e) {
			UiCommons.showBottomFadingText("Delete failed!", getChildren());
			e.printStackTrace(System.err);
		} catch (CorruptDataException e) {
			handleCorruptDb(e);
		} finally {
			close();
		}
	}
}
