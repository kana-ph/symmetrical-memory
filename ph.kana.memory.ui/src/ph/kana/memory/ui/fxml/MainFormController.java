package ph.kana.memory.ui.fxml;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import ph.kana.memory.model.Account;
import ph.kana.memory.model.PinStatus;
import ph.kana.memory.stash.AccountService;
import ph.kana.memory.stash.AuthService;
import ph.kana.memory.stash.StashException;
import ph.kana.memory.ui.fxml.message.LargeCenterText;
import ph.kana.memory.ui.fxml.modal.*;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class MainFormController implements Initializable {

	private final Logger logger = Logger.getLogger(MainFormController.class.getName());
	private HostServices hostServices;

	private final AccountService accountService = AccountService.getInstance();
	private final AuthService authService = AuthService.getInstance();

	@FXML private Pane rootPane;
	@FXML private Pane viewPane;

	@FXML
	public void showAddAccountDialog() {
		showModalWithReload(new SaveAccountModal(), Optional.empty());
	}

	@FXML
	public void showSetPinModal() {
		showModal(new SavePinModal(), null);
	}

	@FXML
	public void openSourceCodeUrl() {
		String url = "https://github.com/kana0011/symmetrical-memory";
		showBottomMessage("Opening web browser...");
		hostServices.showDocument(url);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		PinStatus pinStatus = authService.initializePin();
		if (PinStatus.MISSING == pinStatus) {
			showModal(new ResetModal(), null);
		} else {
			showLoginModal(pinStatus);
			Platform.runLater(this::loadAccounts);
		}
	}

	public void setHostServices(HostServices hostServices) {
		this.hostServices = hostServices;
	}

	private void loadAccounts() {
		try {
			viewPane.getChildren()
					.clear();
			showCenterMessage("Loading...");
			List<Account> accounts = accountService.fetchAccounts();
			if (accounts.isEmpty()) {
				showCenterMessage("No saved accounts!\nClick 'Add' to get started!");
			} else {
				clearCenterMessage();
				accounts.forEach(this::renderAccountCard);
			}
		} catch (StashException e) {
			showBottomMessage("Loading failed!");
			logger.severe(e::getMessage);
		}
	}

	private void renderAccountCard(Account account) {
		Pane pane = new AnchorPane();
		addCssClass(pane, "account-card");
		pane.setPrefHeight(70.0);
		pane.setMinHeight(70.0);
		pane.setMaxHeight(70.0);
		List<Node> children = pane.getChildren();

		Label domainLabel = new Label(account.getDomain());
		children.add(domainLabel);
		addCssClass(domainLabel, "domain-label");
		UiCommons.assignAnchors(domainLabel, 5.0, 100.0, null, 5.0);

		Label usernameLabel = new Label(account.getUsername());
		children.add(usernameLabel);
		addCssClass(usernameLabel, "username-label");
		UiCommons.assignAnchors(usernameLabel, 10.0, 100.0, 15.0, 10.0);

		Button showButton = new Button("Show");
		children.add(showButton);
		addCssClass(showButton, "control");
		showButton.setFocusTraversable(false);
		showButton.setOnAction(event -> showModal(new PasswordRevealModal(), account));
		UiCommons.assignAnchors(showButton, 5.0, 10.0, null, null);

		MenuButton accountMenu = new MenuButton("\u2699");
		children.add(accountMenu);
		addCssClass(accountMenu, "control");
		accountMenu.setFocusTraversable(false);
		UiCommons.assignAnchors(accountMenu, null, 10.0, 5.0, null);

		List<MenuItem> menuItems = accountMenu.getItems();
		MenuItem updateMenuItem = new MenuItem("Update");
		menuItems.add(updateMenuItem);
		updateMenuItem.setOnAction(event -> showModalWithReload(new SaveAccountModal(), Optional.of(account)));

		MenuItem deleteMenuItem = new MenuItem("Delete");
		menuItems.add(deleteMenuItem);
		deleteMenuItem.setOnAction(event -> showModalWithReload(new DeleteAccountModal(), account));

		viewPane.getChildren()
				.add(pane);
	}

	private void addCssClass(Node node, String cssClass) {
		List<String> classes = node.getStyleClass();
		classes.add(cssClass);
	}

	private void showModal(AbstractTilePaneModal modal, Object data) {
		List<Node> rootChildren = rootPane.getChildren();
		rootChildren.add(modal);
		UiCommons.assignAnchors(modal, 0.0, 0.0, 0.0, 0.0);
		modal.showModal(data);
	}

	private void showModalWithReload(AbstractTilePaneModal modal, Object data) {
		modal.setOnClose(this::loadAccounts);
		showModal(modal, data);
	}

	private void showLoginModal(PinStatus pinStatus) {
		var rootChildren = rootPane.getChildren();

		var blur = new AnchorPane();
		var paint = Color.WHITESMOKE
				.deriveColor(0, 1, 0.9, 0.8);
		var background = new Background(new BackgroundFill(paint, null, null));
		blur.setBackground(background);

		rootChildren.add(blur);
		UiCommons.assignAnchors(blur, 0.0, 0.0, 0.0, 0.0);

		var loginModal = new LoginModal();
		loginModal.setOnClose(() -> rootChildren.remove(blur));

		showModal(loginModal, pinStatus);
	}

	private void showCenterMessage(String message) {
		LargeCenterText centerText = new LargeCenterText();
		centerText.setMessage(message);

		clearCenterMessage();
		List<Node> rootChildren = rootPane.getChildren();
		rootChildren.add(2, centerText);
		UiCommons.assignAnchors(centerText, 50.0, 0.0, 0.0, 0.0);
	}

	private void clearCenterMessage() {
		rootPane.getChildren()
				.removeIf(LargeCenterText.class::isInstance);
	}

	private void showBottomMessage(String message) {
		UiCommons.showBottomFadingText(message, rootPane.getChildren());
	}
}
