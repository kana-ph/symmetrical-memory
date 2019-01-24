package ph.kana.memory.ui.fxml;

import javafx.animation.TranslateTransition;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import ph.kana.memory.ui.fxml.message.FadingNotificationText;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class UiCommons {

	private UiCommons() {}

	private static final Set<Node> NODE_ANIMATION_LOCK = new HashSet<>();

	public static void assignAnchors(Node node, Double top, Double right, Double bottom, Double left) {
		AnchorPane.setTopAnchor(node, top);
		AnchorPane.setRightAnchor(node, right);
		AnchorPane.setBottomAnchor(node, bottom);
		AnchorPane.setLeftAnchor(node, left);
	}

	public static void forceNumericalInput(TextField numericalTextField) {
		StringProperty pinTextProperty = numericalTextField.textProperty();
		pinTextProperty.addListener((observableString, oldValue, newValue) -> {
			if (!newValue.matches("\\d*")) {
				numericalTextField.setText(newValue.replaceAll("[^\\d]", ""));
			}
		});
	}

	public static <TController> void loadFxmlFile(TController rootController, String fxmlPath) {
		FXMLLoader loader = new FXMLLoader(rootController.getClass().getResource(fxmlPath));
		loader.setController(rootController);
		loader.setRoot(rootController);

		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}

	public static void showBottomFadingText(String text, List<Node> nodeList) {
		FadingNotificationText fadingText = new FadingNotificationText(nodeList::remove);

		nodeList.stream()
				.filter(FadingNotificationText.class::isInstance)
				.forEach(ft -> assignAnchors(ft, null, 20.0, AnchorPane.getBottomAnchor(ft) + 28.0, 20.0));

		nodeList.add(fadingText);
		assignAnchors(fadingText, null, 20.0, 20.0, 20.0);
		fadingText.showText(text);
	}

	public static void shake(Node node) {
		if (verifyAnimationLock(node)) {
			return;
		}

		var translateTransition  = new TranslateTransition();
		translateTransition.setDuration(Duration.millis(100.0));
		translateTransition.setNode(node);

		translateTransition.setByX(5);
		translateTransition.setCycleCount(4);
		translateTransition.setAutoReverse(true);

		translateTransition.setOnFinished(e -> removeAnimationLock(node));

		translateTransition.play();
	}

	public static void bindPasswordToggle(PasswordField passwordField, TextField textField, CheckBox toggle) {
		textField.textProperty()
				.bindBidirectional(passwordField.textProperty());
		textField.translateXProperty()
				.bindBidirectional(passwordField.translateXProperty());

		textField.visibleProperty()
				.bind(toggle.selectedProperty());
	}

	private static boolean verifyAnimationLock(Node node) {
		var locked = NODE_ANIMATION_LOCK.contains(node);

		if (!locked) {
			NODE_ANIMATION_LOCK.add(node);
		}
		return locked;
	}

	private static void removeAnimationLock(Node node) {
		NODE_ANIMATION_LOCK.remove(node);
	}
}
