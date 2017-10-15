package ph.kana.memory.ui.fxml;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import ph.kana.memory.codec.CodecOperationException;
import ph.kana.memory.codec.PasswordCodec;
import ph.kana.memory.model.Account;
import ph.kana.memory.stash.AccountService;
import ph.kana.memory.stash.AuthService;
import ph.kana.memory.stash.StashException;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class MainFormController implements Initializable {

	private final Logger logger = Logger.getLogger(MainFormController.class.getName());
	private final AccountService accountService = new AccountService();
	private final AuthService authService = new AuthService();
	private final PasswordCodec passwordCodec = new PasswordCodec();

	@FXML private Pane loginPane;
	@FXML private TextField pinTextBox;

	@FXML private Pane viewPane;

	@FXML private Pane saveAccountPane;
	@FXML private Label saveAccountPaneTitle;
	@FXML private Label accountIdValue;
	@FXML private TextField domainTextBox;
	@FXML private TextField usernameTextBox;
	@FXML private TextField unmaskedPasswordTextBox;
	@FXML private PasswordField maskedPasswordTextBox;
	@FXML private CheckBox maskPasswordToggle;

	@FXML private Pane centerMessagePane;
	@FXML private Label centerMessageLabel;

	@FXML private Pane bottomMessagePane;
	@FXML private Label bottomMessageLabel;

	@FXML private Pane passwordRevealPane;
	@FXML private TextField passwordRevealTextBox;
	@FXML private Label passwordValue;

	@FXML
	public void validatePin() {
		String pin = pinTextBox.getText();
		if (pin.isEmpty()) {
			return;
		}
		try {
			if (authService.checkValidPin(pin)) {
				loginPane.setVisible(false);
			} else {
				pinTextBox.setText("");
			}
		} catch (StashException e) {
			showBottomMessage("Cannot validate PIN");
			e.printStackTrace(System.err);
		}
	}

	@FXML
	public void showAddAccountDialog() {
		saveAccountPaneTitle.setText("Add Account");
		accountIdValue.setText("");
		saveAccountPane.setVisible(true);
	}

	@FXML
	public void saveAccount() {
		String accountId = accountIdValue.getText();
		String domain = domainTextBox.getText();
		String username = usernameTextBox.getText();
		String rawPassword = maskedPasswordTextBox.getText();
		try {
			accountService.saveAccount(accountId, domain, username, rawPassword);

			showBottomMessage("Saving success!");
			closeSaveAccountModal();
			loadAccounts();
		} catch (StashException e) {
			showBottomMessage("Saving failed!");
			e.printStackTrace(System.err);
		} finally {
			domainTextBox.setText("");
			usernameTextBox.setText("");
			maskedPasswordTextBox.setText("");
		}
	}

	@FXML
	public void closeSaveAccountModal() {
		saveAccountPane.setVisible(false);
	}

	@FXML
	public void closePasswordRevealModal() {
		passwordValue.setText("");
		passwordRevealPane.setVisible(false);
	}

	@FXML
	public void showPassword() {
		passwordRevealTextBox.setText(passwordValue.getText());
	}

	@FXML
	public void hidePassword() {
		passwordRevealTextBox.setText("");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Platform.runLater(this::forcePinNumberInput);
		Platform.runLater(this::bindPasswordToggle);
		Platform.runLater(this::loadAccounts);
	}

	private void forcePinNumberInput() {
		StringProperty pinTextProperty = pinTextBox.textProperty();
		pinTextProperty.addListener((observableString, oldValue, newValue) -> {
			if (!newValue.matches("\\d*")) {
				pinTextBox.setText(newValue.replaceAll("[^\\d]", ""));
			}
		});
	}

	private void bindPasswordToggle() {
		unmaskedPasswordTextBox.textProperty()
				.bindBidirectional(maskedPasswordTextBox.textProperty());
		maskedPasswordTextBox.visibleProperty()
				.bind(maskPasswordToggle.selectedProperty().not());
	}

	private void loadAccounts() {
		try {
			viewPane.getChildren()
					.clear();
			showCenterMessage("Loading...");
			List<Account> accounts = accountService.fetchAccounts();
			if (accounts.isEmpty()) {
				showCenterMessage("No saved accounts");
			} else {
				centerMessagePane.setVisible(false);
				accounts.stream()
						.forEach(this::renderAccountCard);
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
		List<Node> children = pane.getChildren();

		Label usernameLabel = new Label(account.getUsername());
		children.add(usernameLabel);
		addCssClass(usernameLabel, "username-label");
		assignAnchors(usernameLabel, 15.0, 100.0, null, 15.0);

		Label domainLabel = new Label(account.getDomain());
		children.add(domainLabel);
		addCssClass(domainLabel, "domain-label");
		assignAnchors(domainLabel, null, 100.0, 5.0, 15.0);

		Button showButton = new Button("Show");
		children.add(showButton);
		addCssClass(showButton, "control");
		showButton.setOnAction(event -> showPasswordRevealForAccount(account));
		assignAnchors(showButton, 5.0, 10.0, null, null);

		MenuButton accountMenu = new MenuButton("...");
		children.add(accountMenu);
		addCssClass(accountMenu, "control");
		List<MenuItem> menuItems = accountMenu.getItems();
		MenuItem updateMenuItem = new MenuItem("Update");
		menuItems.add(updateMenuItem);
		updateMenuItem.setOnAction(event -> showSaveDialogForAccount(account));

		MenuItem deleteMenuItem = new MenuItem("Delete");
		menuItems.add(deleteMenuItem);
		deleteMenuItem.setOnAction(event -> {
			// TODO: delete account
		});
		assignAnchors(accountMenu, null, 10.0, 5.0, null);

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

	private void showSaveDialogForAccount(Account account) {
		accountIdValue.setText(account.getId());
		domainTextBox.setText(account.getDomain());
		usernameTextBox.setText(account.getUsername());
		maskedPasswordTextBox.setText("");

		saveAccountPaneTitle.setText("Update account");
		saveAccountPane.setVisible(true);
	}

	private void showPasswordRevealForAccount(Account account) {
		String encryptedPassword = account.getEncryptedPassword();
		String timestamp = Long.toString(account.getSaveTimestamp());
		try {
			String clearPassword = passwordCodec.decrypt(encryptedPassword, timestamp);
			passwordValue.setText(clearPassword);
			passwordRevealPane.setVisible(true);
		} catch (CodecOperationException e) {
			closePasswordRevealModal();
			showBottomMessage("Something went wrong.");
			logger.severe(e::getMessage);
		}
	}

	private void showCenterMessage(String message) {
		centerMessageLabel.setText(message);
		centerMessagePane.setVisible(true);
	}

	private void showBottomMessage(String message) {
		bottomMessageLabel.setText(message);

		bottomMessagePane.setOpacity(1.0);
		FadeTransition fadeTransition = new FadeTransition(Duration.millis(200.0), bottomMessagePane);
		fadeTransition.setDelay(Duration.millis(3000.0));
		fadeTransition.setFromValue(1.0);
		fadeTransition.setToValue(0.0);
		fadeTransition.setCycleCount(1);
		fadeTransition.setAutoReverse(false);
		fadeTransition.play();
	}
}
