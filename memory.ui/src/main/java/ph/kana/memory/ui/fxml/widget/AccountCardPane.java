package ph.kana.memory.ui.fxml.widget;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import ph.kana.memory.account.CorruptDataException;
import ph.kana.memory.model.Account;
import ph.kana.memory.ui.fxml.UiCommons;
import ph.kana.memory.ui.fxml.modal.AbstractTilePaneModal;
import ph.kana.memory.ui.fxml.modal.DeleteAccountModal;
import ph.kana.memory.ui.fxml.modal.PasswordRevealModal;
import ph.kana.memory.ui.fxml.modal.SaveAccountModal;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class AccountCardPane extends AnchorPane {

	@FXML private Label domainLabel;
	@FXML private Label usernameLabel;

	private final static Runnable DO_NOTHING = () -> {};

	private final Account account;
	private final Pane rootPane;
	private final Consumer<CorruptDataException> handleCorruptDb;
	private Runnable onDeleteAccount;

	public AccountCardPane(Account account, Pane rootPane, Consumer<CorruptDataException> handleCorruptDb) {
		UiCommons.loadFxmlFile(this, "account-card-pane.fxml");

		this.account = account;
		this.rootPane = rootPane;
		this.handleCorruptDb = handleCorruptDb;
		this.onDeleteAccount = DO_NOTHING;

		domainLabel.setText(account.getDomain());
		usernameLabel.setText(account.getUsername());
		setUserData(account);
	}

	@FXML
	public void revealPassword() {
		showModal(new PasswordRevealModal(), account);
	}

	@FXML
	public void update() {
		var modal = new SaveAccountModal();
		modal.setOnClose(this::updateAccountCard);
		showModal(modal, account);
	}

	@FXML
	public void delete() {
		var modal = new DeleteAccountModal();
		modal.setOnClose(deleted -> {
			if (deleted) {
				getContainer().remove(this);
				onDeleteAccount.run();
			}
		});
		showModal(modal, account);
	}

	public void setOnDeleteAccount(Runnable onDeleteAccount) {
		this.onDeleteAccount = onDeleteAccount;
	}

	private List<Node> getContainer() {
		var parent = (Pane) getParent();
		return parent.getChildren();
	}

	private <T, R> void showModal(AbstractTilePaneModal<T, R> modal, T data) {
		List<Node> rootChildren = rootPane.getChildren();
		rootChildren.add(modal);
		UiCommons.assignAnchors(modal, 0.0, 0.0, 0.0, 0.0);

		modal.setOnHandleCorruptDb(handleCorruptDb);
		modal.showModal(data);
	}

	private void updateAccountCard(Account account) {
		if (Objects.isNull(account)) {
			return;
		}
		domainLabel.setText(account.getDomain());
		usernameLabel.setText(account.getUsername());
	}
}
