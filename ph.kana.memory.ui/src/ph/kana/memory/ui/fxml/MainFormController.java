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
import ph.kana.memory.ui.fxml.modal.*;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class MainFormController implements Initializable {

	private final Logger logger = Logger.getLogger(MainFormController.class.getName());
	private final AccountService accountService = AccountService.getInstance();
	private HostServices hostServices;

	@FXML private Pane rootPane;
	@FXML private Pane viewPane;

	@FXML private Pane centerMessagePane;
	@FXML private Label centerMessageLabel;

	@FXML private PasswordRevealModal passwordRevealModal;
	@FXML private DeleteAccountModal deleteAccountModal;
	@FXML private SaveAccountModal saveAccountModal;
	@FXML private SavePinModal savePinModal;

	@FXML
	public void showAddAccountDialog() {
		saveAccountModal.setOnClose(this::loadAccounts);
		saveAccountModal.showModal(Optional.empty());
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		showModal(new LoginModal(), null);
		Platform.runLater(this::loadAccounts);
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
		showButton.setOnAction(event -> passwordRevealModal.showModal(account));
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

	private  void showModal(AbstractTilePaneModal modal, Object data) {
		List<Node> rootChildren = rootPane.getChildren();
		rootChildren.add(modal);
		UiCommons.assignAnchors(modal, 0.0, 0.0, 0.0, 0.0);
		modal.showModal(data);
	}

	private void showSaveDialogForAccount(Account account) {
		saveAccountModal.setOnClose(this::loadAccounts);
		saveAccountModal.showModal(Optional.of(account));
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
