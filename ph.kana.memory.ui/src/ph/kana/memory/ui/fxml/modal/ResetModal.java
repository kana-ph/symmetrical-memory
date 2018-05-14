package ph.kana.memory.ui.fxml.modal;

import javafx.fxml.FXML;

public class ResetModal extends AbstractTilePaneModal<Void> {

	public ResetModal() {
		super("reset-modal.fxml");
	}

	@Override
	public void showModal(Void data) {
		setVisible(true);
	}

	@FXML
	public void revertApplication() {
		// TODO implement?
	}
}
