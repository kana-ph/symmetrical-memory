package ph.kana.memory.ui.fxml;

import javafx.animation.FadeTransition;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import ph.kana.memory.model.Account;
import ph.kana.memory.stash.AccountService;
import ph.kana.memory.stash.AuthService;
import ph.kana.memory.stash.StashException;
import ph.kana.memory.ui.fxml.modal.DeleteAccountModal;
import ph.kana.memory.ui.fxml.modal.LoginModal;
import ph.kana.memory.ui.fxml.modal.PasswordRevealModal;
import ph.kana.memory.ui.fxml.modal.SavePinModal;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class MainFormController implements Initializable {

	private final Logger logger = Logger.getLogger(MainFormController.class.getName());
	private final AccountService accountService = new AccountService();
	private final AuthService authService = AuthService.getInstance();
	private HostServices hostServices;

	@FXML private Pane rootPane;
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

	@FXML private LoginModal loginModal;
	@FXML private PasswordRevealModal passwordRevealModal;
	@FXML private DeleteAccountModal deleteAccountModal;
	@FXML private SavePinModal savePinModal;

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
		if (domain.isEmpty() || username.isEmpty() || rawPassword.isEmpty()) {
			showBottomMessage("All fields required!");
			return;
		}
		try {
			accountService.saveAccount(accountId, domain, username, rawPassword);

			showBottomMessage("Saving success!");
			loadAccounts();
			closeSaveAccountModal();
		} catch (StashException e) {
			showBottomMessage("Saving failed!");
			e.printStackTrace(System.err);
			closeSaveAccountModal();
		} finally {
			domainTextBox.setText("");
			usernameTextBox.setText("");
			maskedPasswordTextBox.setText("");
		}
	}

	@FXML
	public void showSetPinModal() {
		savePinModal.showModal(null);
	}

	@FXML
	public void openSourceCodeUrl() {
		String url = "https://github.com/kana0011/symmetrical-memory";
		showBottomMessage("Opening web browser...");
		hostServices.showDocument(url);
	}

	@FXML
	public void closeSaveAccountModal() {
		saveAccountPane.setVisible(false);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Platform.runLater(this::bindPasswordToggle);
		Platform.runLater(this::loadAccounts);
	}

	public void setHostServices(HostServices hostServices) {
		this.hostServices = hostServices;
	}

	@Deprecated(forRemoval = true)
	private void forceNumericalInput(TextField numericalTextField) {
		StringProperty pinTextProperty = numericalTextField.textProperty();
		pinTextProperty.addListener((observableString, oldValue, newValue) -> {
			if (!newValue.matches("\\d*")) {
				numericalTextField.setText(newValue.replaceAll("[^\\d]", ""));
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
		pane.setMaxHeight(70.0);
		List<Node> children = pane.getChildren();

		Label usernameLabel = new Label(account.getUsername());
		children.add(usernameLabel);
		addCssClass(usernameLabel, "username-label");
		UiCommons.assignAnchors(usernameLabel, 15.0, 100.0, null, 15.0);

		Label domainLabel = new Label(account.getDomain());
		children.add(domainLabel);
		addCssClass(domainLabel, "domain-label");
		UiCommons.assignAnchors(domainLabel, null, 100.0, 5.0, 15.0);

		Button showButton = new Button("Show");
		children.add(showButton);
		addCssClass(showButton, "control");
		showButton.setFocusTraversable(false);
		showButton.setOnAction(event -> showPasswordRevealForAccount(account));
		UiCommons.assignAnchors(showButton, 5.0, 10.0, null, null);

		MenuButton accountMenu = new MenuButton("\u2699");
		children.add(accountMenu);
		addCssClass(accountMenu, "control");
		accountMenu.setFocusTraversable(false);
		UiCommons.assignAnchors(accountMenu, null, 10.0, 5.0, null);

		List<MenuItem> menuItems = accountMenu.getItems();
		MenuItem updateMenuItem = new MenuItem("Update");
		menuItems.add(updateMenuItem);
		updateMenuItem.setOnAction(event -> showSaveDialogForAccount(account));

		MenuItem deleteMenuItem = new MenuItem("Delete");
		menuItems.add(deleteMenuItem);
		deleteMenuItem.setOnAction(event -> showDeleteModal(account));

		viewPane.getChildren()
				.add(pane);
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
			String clearPassword = authService.decryptPassword(encryptedPassword, timestamp);
			passwordRevealModal.showModal(clearPassword);
		} catch (StashException e) {
			passwordRevealModal.close();
			showBottomMessage("Something went wrong.");
			logger.severe(e::getMessage);
		}
	}

	private void showCenterMessage(String message) {
		centerMessageLabel.setText(message);
		centerMessagePane.setVisible(true);
	}

	private void showDeleteModal(Account account) {
		deleteAccountModal.setOnClose(this::loadAccounts);
		deleteAccountModal.showModal(account);
	}

	private void showBottomMessage(String message) {
		UiCommons.showBottomFadingText(message, rootPane.getChildren());
	}
}
