package ph.kana.memory.ui.fxml.modal;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import ph.kana.memory.stash.CorruptDataException;
import ph.kana.memory.ui.fxml.UiCommons;

import java.util.function.Consumer;

public abstract class AbstractTilePaneModal<TData> extends TilePane {

	private Consumer<TData> onClose = null;
	private Consumer<CorruptDataException> onHandleCorruptDb = null;

	private TData result;

	AbstractTilePaneModal(String fxmlPath) {
		UiCommons.loadFxmlFile(this, fxmlPath);
		setVisible(false);
	}

	public abstract void showModal(TData data);

	@FXML
	public void close() {
		setVisible(false);
		if (null != onClose) {
			onClose.accept(result);
		}

		Pane parent = (Pane) getParent();
		parent.getChildren()
				.remove(this);
	}

	public void setOnClose(Consumer<TData> onClose) {
		this.onClose = onClose;
	}

	protected void setResult(TData result) {
		this.result = result;
	}

	public void setOnHandleCorruptDb(Consumer<CorruptDataException>  onHandleCorruptDb) {
		this.onHandleCorruptDb = onHandleCorruptDb;
	}

	void showBottomFadingText(String text) {
		Pane parent = (Pane) getParent();
		UiCommons.showBottomFadingText(text, parent.getChildren());
	}

	void handleCorruptDb(CorruptDataException e) {
		if (null != onHandleCorruptDb) {
			onHandleCorruptDb.accept(e);
		}
	}
}
