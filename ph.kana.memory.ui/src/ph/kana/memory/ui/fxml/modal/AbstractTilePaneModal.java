package ph.kana.memory.ui.fxml.modal;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import ph.kana.memory.ui.fxml.UiCommons;

public abstract class AbstractTilePaneModal<TData> extends TilePane {

	private Runnable onClose = null;

	AbstractTilePaneModal(String fxmlPath) {
		UiCommons.loadFxmlFile(this, fxmlPath);
		setVisible(false);
	}

	public abstract void showModal(TData data);

	@FXML
	public void close() {
		setVisible(false);
		if (null != onClose) {
			onClose.run();
		}

		Pane parent = (Pane) getParent();
		parent.getChildren()
				.remove(this);
	}

	public void setOnClose(Runnable onClose) {
		this.onClose = onClose;
	}

	void showBottomFadingText(String text) {
		Pane parent = (Pane) getParent();
		UiCommons.showBottomFadingText(text, parent.getChildren());
	}
}
