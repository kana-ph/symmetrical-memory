package ph.kana.memory.ui.fxml.modal;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import ph.kana.memory.stash.AuthService;
import ph.kana.memory.stash.StashException;

import static ph.kana.memory.ui.fxml.UiCommons.forceNumericalInput;

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
	}
}
