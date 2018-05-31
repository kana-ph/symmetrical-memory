package ph.kana.memory.ui.fxml;

import javafx.application.HostServices;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import ph.kana.memory.model.Account;
import ph.kana.memory.model.PinStatus;
import ph.kana.memory.stash.AccountService;
import ph.kana.memory.stash.AuthService;
import ph.kana.memory.stash.CorruptDataException;
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
	private IdleMonitor idleMonitor = null;

	private final AccountService accountService = AccountService.getInstance();
	private final AuthService authService = AuthService.getInstance();

	private final Duration SESSION_EXPIRE_DURATION = Duration.seconds(30);

	@FXML private Pane rootPane;
	@FXML private Pane viewPane;

	@FXML private TextField filterTextBox;

	@FXML private ToggleGroup sortGroup;

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

	@FXML
	public void clearSearchFilter() {
		filterTextBox.setText("");
		loadAccounts();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		showLoginModal(true);

		filterTextBox.textProperty()
				.addListener(this::filterAccounts);

		sortGroup.selectedToggleProperty()
				.addListener(this::updateSort);
	}

	public void setHostServices(HostServices hostServices) {
		this.hostServices = hostServices;
	}

	private void filterAccounts(ObservableValue<? extends String> observable, String oldText, String newText) {
		viewPane.getChildren()
				.clear();
		showCenterMessage("Searching...");

		if (newText.isEmpty()) {
			loadAccounts();
		} else {
			try {
				var accounts = accountService.searchAccounts(newText);
				renderAccounts(accounts, String.format("No search results for\n'%s'", cutString(newText)));
			} catch (StashException e) {
				showBottomMessage("Loading failed!");
				logger.severe(e::getMessage);
			} catch (CorruptDataException e) {
				handleCorruptDb(e);
			}
		}
	}

	private void updateSort(ObservableValue<? extends Toggle> observableValue, Toggle oldToggle, Toggle newToggle) {
		// TODO implement
	}

	private void loadAccounts() {
		viewPane.getChildren()
				.clear();
		showCenterMessage("Loading...");
		try {
			var accounts = accountService.fetchAccounts();
			renderAccounts(accounts, "No saved accounts!\nClick 'Add' to get started!");
		} catch (StashException e) {
			showBottomMessage("Loading failed!");
			logger.severe(e::getMessage);
		} catch (CorruptDataException e) {
			handleCorruptDb(e);
		}
	}

	private void renderAccounts(List<Account> accounts, String emptyAccountsMessage) {
		if (accounts.isEmpty()) {
			showCenterMessage(emptyAccountsMessage);
		} else {
			clearCenterMessage();
			accounts.forEach(this::renderAccountCard);
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

	private <T> void showModal(AbstractTilePaneModal<T> modal, T data) {
		List<Node> rootChildren = rootPane.getChildren();
		rootChildren.add(modal);
		UiCommons.assignAnchors(modal, 0.0, 0.0, 0.0, 0.0);

		modal.setOnHandleCorruptDb(this::handleCorruptDb);
		modal.showModal(data);
	}

	private <T> void showModalWithReload(AbstractTilePaneModal<T> modal, T data) {
		modal.setOnClose(this::loadAccounts);
		showModal(modal, data);
	}

	private void showLoginModal(boolean startup) {
		try {
			PinStatus pinStatus = authService.initializePin();

			var loginModal = new LoginModal();

			if (startup) {
				loginModal.setOnClose(() -> {
					loadAccounts();
					initializeIdleMonitor();
				});
			} else {
				idleMonitor.stopMonitoring();
				loginModal.setOnClose(idleMonitor::startMonitoring);
			}

			showModal(loginModal, pinStatus);
		} catch (CorruptDataException e) {
			handleCorruptDb(e);
		}
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

	private void initializeIdleMonitor() {
		if (null == idleMonitor) {
			idleMonitor = new IdleMonitor(SESSION_EXPIRE_DURATION, () -> showLoginModal(false));
			idleMonitor.register(rootPane.getScene(), Event.ANY);
			idleMonitor.startMonitoring();
		}
	}

	private String cutString(String string) {
		if (string.length() > 18) {
			return string.substring(0, 18) + '\u2026';
		}
		return string;
	}

	private void handleCorruptDb(CorruptDataException e) {
		showModal(new ResetModal(), null);
		logger.severe(e::getMessage);
	}
}
