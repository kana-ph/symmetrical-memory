package ph.kana.memory.ui.fxml;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import ph.kana.memory.model.Account;
import ph.kana.memory.stash.AccountService;
import ph.kana.memory.stash.StashException;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class MainFormController implements Initializable {

	private final Logger logger = Logger.getLogger(MainFormController.class.getName());
	private final AccountService accountService = new AccountService();

	@FXML
	private Pane viewPane;

	@FXML
	private Pane saveAccountPane;

	@FXML
	private Label saveAccountPaneTitle;

	@FXML
	private TextField domainTextBox;

	@FXML
	private TextField usernameTextBox;

	@FXML
	private TextField unmaskedPasswordTextBox;

	@FXML
	private PasswordField maskedPasswordTextBox;

	@FXML
	private CheckBox maskPasswordToggle;

	@FXML
	public void showAddAccountDialog() {
		saveAccountPaneTitle.setText("Add Account");
		saveAccountPane.setVisible(true);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Platform.runLater(this::bindPasswordToggle);
		Platform.runLater(this::loadAccounts);
	}

	private void bindPasswordToggle() {
		unmaskedPasswordTextBox.textProperty()
				.bindBidirectional(maskedPasswordTextBox.textProperty());
		unmaskedPasswordTextBox.visibleProperty()
				.bind(maskPasswordToggle.selectedProperty().not());
	}

	private void loadAccounts() {
		try {
			List<Account> accounts = accountService.fetchAccounts();
			if (accounts.isEmpty()) {
				// TODO: show "nothing yet" message
			} else {
				// TODO: remove "loading" message
				accounts.stream()
						.forEach(this::renderAccountCard);
			}
		} catch (StashException e) {
			// TODO: show error in GUI
			logger.severe(e::getMessage);
		}
	}

	private void renderAccountCard(Account account) {
		Pane pane = new AnchorPane();
		addCssClass(pane, "account-card");
		pane.setPrefHeight(70.0);
		pane.setMinHeight(70.0);
		List<Node> children = pane.getChildren();

		Label usernameLabel = new Label(account.getUsername());
		children.add(usernameLabel);
		addCssClass(usernameLabel, "username-label");
		assignAnchors(usernameLabel, 20.0, 100.0, null, 15.0);

		Label domainLabel = new Label(account.getDomain());
		children.add(domainLabel);
		addCssClass(domainLabel, "domain-label");
		assignAnchors(domainLabel, null, null, 0.0, 15.0);

		Button showButton = new Button("Show");
		children.add(showButton);
		showButton.setOnAction(event -> {
			String encryptedPassword = account.getEncryptedPassword();
			// TODO: do something with password
		});
		assignAnchors(showButton, 5.0, 10.0, null, null);

		MenuButton accountMenu = new MenuButton("...");
		children.add(accountMenu);
		List<MenuItem> menuItems = accountMenu.getItems();
		MenuItem updateMenuItem = new MenuItem("Update");
		menuItems.add(updateMenuItem);
		updateMenuItem.setOnAction(event -> {
			// TODO: update account
		});
		MenuItem deleteMenuItem = new MenuItem("Delete");
		menuItems.add(deleteMenuItem);
		deleteMenuItem.setOnAction(event -> {
			// TODO: delete account
		});
		assignAnchors(showButton, null, 10.0, 5.0, null);

		viewPane.getChildren()
				.add(pane);
	}

	private void assignAnchors(Node node, Double top, Double right, Double bottom, Double left) {
		AnchorPane.setTopAnchor(node, top);
		AnchorPane.setRightAnchor(node, right);
		AnchorPane.setBottomAnchor(node, bottom);
		AnchorPane.setLeftAnchor(node, left);
	}

	private void addCssClass(Node node, String cssClass) {
		List<String> classes = node.getStyleClass();
		classes.add(cssClass);
	}
}
