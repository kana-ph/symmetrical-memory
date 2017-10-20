package ph.kana.memory.ui.fxml.modal;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;

import java.io.IOException;

import static ph.kana.memory.ui.fxml.UiCommons.loadFxmlFile;

abstract class AbstractTilePaneModal<TData> extends TilePane {

	AbstractTilePaneModal(String fxmlPath) {
		loadFxmlFile(this, fxmlPath);
	}

	public abstract void showModal(TData data);

	protected void forceNumericalInput(TextField numericalTextField) {
		StringProperty pinTextProperty = numericalTextField.textProperty();
		pinTextProperty.addListener((observableString, oldValue, newValue) -> {
			if (!newValue.matches("\\d*")) {
				numericalTextField.setText(newValue.replaceAll("[^\\d]", ""));
			}
		});
	}
}
