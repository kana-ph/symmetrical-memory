package ph.kana.memory.ui.fxml.modal;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import ph.kana.memory.ui.fxml.UiCommons;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

abstract class AbstractTilePaneModal<TData> extends TilePane {

	private Optional<Runnable> onClose = Optional.empty();

	AbstractTilePaneModal(String fxmlPath) {
		UiCommons.loadFxmlFile(this, fxmlPath);
	}

	public abstract void showModal(TData data);

	@FXML
	public void close() {
		setVisible(false);
		onClose.ifPresent(Runnable::run);
	}

	public void setOnClose(Runnable onClose) {
		this.onClose = Optional.ofNullable(onClose);
	}

	protected void showBottomFadingText(String text) {
		Pane parent = (Pane) getParent();
		UiCommons.showBottomFadingText(text, parent.getChildren());
	}
}
