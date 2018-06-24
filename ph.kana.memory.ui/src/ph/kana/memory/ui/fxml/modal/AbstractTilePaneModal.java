package ph.kana.memory.ui.fxml.modal;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import ph.kana.memory.stash.CorruptDataException;
import ph.kana.memory.ui.fxml.UiCommons;

import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractTilePaneModal<TData, TResult> extends TilePane {

	private Consumer<TResult> onClose = null;
	private Consumer<CorruptDataException> onHandleCorruptDb = null;

	private TResult result;

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

	public void setOnClose(Consumer<TResult> onClose) {
		this.onClose = onClose;
	}

	public void setOnHandleCorruptDb(Consumer<CorruptDataException>  onHandleCorruptDb) {
		this.onHandleCorruptDb = onHandleCorruptDb;
	}

	void setResult(TResult result) {
		this.result = result;
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

	boolean validateFields(Map<TextField, String> fields) {
		var valid = true;
		for (var field : fields.keySet()) {
			var fieldValid = !field.getText().isEmpty();
			valid &= fieldValid;

			if (!fieldValid) {
				UiCommons.shake(field);
				showBottomFadingText(fields.get(field));
			}
		}

		return valid;
	}
}
