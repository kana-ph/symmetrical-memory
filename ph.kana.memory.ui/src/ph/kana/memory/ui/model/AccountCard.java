package ph.kana.memory.ui.model;

import javafx.scene.layout.Pane;
import ph.kana.memory.model.Account;

public class AccountCard {
	private final Account account;
	private final Pane card;

	public AccountCard(Pane card, Account account) {
		this.card = card;
		this.account = account;
	}

	public void setVisible(boolean visible) {
		card.setVisible(visible);
	}

	public Account getAccount() {
		return account;
	}

	public Pane getCard() {
		return card;
	}

	public boolean isVisible() {
		return card.isVisible();
	}
}
