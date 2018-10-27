module ph.kana.memory.ui {
	requires java.logging;
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires ph.kana.memory.stash;
	requires ph.kana.memory.common;

	opens ph.kana.memory.ui to javafx.graphics;
	opens ph.kana.memory.ui.fxml to javafx.fxml;
	opens ph.kana.memory.ui.fxml.message to javafx.fxml, javafx.controls;
	opens ph.kana.memory.ui.fxml.modal to javafx.fxml, javafx.controls;
	opens ph.kana.memory.ui.fxml.widget to javafx.fxml, javafx.controls;
}
