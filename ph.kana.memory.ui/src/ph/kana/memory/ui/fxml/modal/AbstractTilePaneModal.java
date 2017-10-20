package ph.kana.memory.ui.fxml.modal;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;

import java.io.IOException;
import java.util.Optional;

import static ph.kana.memory.ui.fxml.UiCommons.loadFxmlFile;

abstract class AbstractTilePaneModal<TData> extends TilePane {

	private Optional<Runnable> onClose = Optional.empty();

	AbstractTilePaneModal(String fxmlPath) {
		loadFxmlFile(this, fxmlPath);
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
}
