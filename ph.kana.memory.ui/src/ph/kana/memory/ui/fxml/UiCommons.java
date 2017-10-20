package ph.kana.memory.ui.fxml;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.util.List;

public final class UiCommons {

	private UiCommons() {}

	public static void assignAnchors(Node node, Double top, Double right, Double bottom, Double left) {
		AnchorPane.setTopAnchor(node, top);
		AnchorPane.setRightAnchor(node, right);
		AnchorPane.setBottomAnchor(node, bottom);
		AnchorPane.setLeftAnchor(node, left);
	}

	public static void showBottomFadingText(String text, List<Node> nodeList) {
		FadingNotificationText fadingText = new FadingNotificationText();

		nodeList.add(fadingText);
		assignAnchors(fadingText, null, 20.0, 20.0, 20.0);
		fadingText.showText(text, nodeList::remove);
	}
}
