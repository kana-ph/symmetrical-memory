package ph.kana.memory.ui.fxml;

import javafx.application.HostServices;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import ph.kana.memory.model.Account;
import ph.kana.memory.stash.AccountService;
import ph.kana.memory.stash.AuthService;
import ph.kana.memory.stash.CorruptDataException;
import ph.kana.memory.stash.StashException;
import ph.kana.memory.type.LoginFlag;
import ph.kana.memory.type.SortColumn;
import ph.kana.memory.ui.fxml.message.LargeCenterText;
import ph.kana.memory.ui.fxml.modal.*;
import ph.kana.memory.ui.model.AccountCard;
import ph.kana.memory.ui.model.AccountComparator;

import java.net.URL;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MainFormController implements Initializable {

	private final Logger logger = Logger.getLogger(MainFormController.class.getName());

	private HostServices hostServices;
	private IdleMonitor idleMonitor = null;

	private final AccountService accountService = AccountService.getInstance();
	private final AuthService authService = AuthService.getInstance();

	private Comparator<Account> activeComparator = AccountComparator.of((SortColumn.DATE_ADDED));
	private SortedMap<Account, AccountCard> accountCards =
			new TreeMap<>(activeComparator);

	private final Duration SESSION_EXPIRE_DURATION = Duration.seconds(30);

	@FXML private Pane rootPane;
	@FXML private Pane viewPane;

	@FXML private TextField filterTextBox;

	@FXML private ToggleGroup sortGroup;

	@FXML
	public void showAddAccountDialog() {
		var saveAccountModal = new SaveAccountModal();
		saveAccountModal.setOnClose(account -> {
			if (Objects.nonNull(account)) {
				var accountCard = renderAccountCard(account);
				accountCards.put(account, accountCard);
			}
		});
		showModal(saveAccountModal, null);
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
		filterAccounts("");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		showLoginModal(true);

		filterTextBox.textProperty()
				.addListener((observable, oldValue, newValue) -> filterAccounts(newValue));

		sortGroup.selectedToggleProperty()
				.addListener((observable, oldValue, newValue) -> updateSort());
	}

	public void setHostServices(HostServices hostServices) {
		this.hostServices = hostServices;
	}

	private void filterAccounts(String searchString) {
		viewPane.getChildren()
				.clear();
		showCenterMessage("Searching...");

		if (searchString.isEmpty()) {
			accountCards.values()
					.forEach(this::renderAccountCard);
		} else {
			var filteredAccounts = accountCards.keySet()
					.stream()
					.filter(account -> matchesDomainOrUsername(account, searchString))
					.map(accountCards::get)
					.collect(Collectors.toUnmodifiableList());
			if (filteredAccounts.isEmpty()) {
				showCenterMessage(String.format("No search results for\n'%s'", cutString(searchString)));
			} else {
				filteredAccounts
						.forEach(this::renderAccountCard);
				clearCenterMessage();
			}
		}
	}

	private void updateSort() {
		var selectedSort = sortGroup.getSelectedToggle();
		var data = selectedSort.getUserData().toString();
		var sortColumn = SortColumn.valueOf(data);

		activeComparator = AccountComparator.of(sortColumn);

		var updatedSortMap = new TreeMap<Account, AccountCard>(activeComparator);
		updatedSortMap.putAll(accountCards);
		accountCards = updatedSortMap;

		viewPane.getChildren()
				.clear();
		renderAccounts(accountCards.keySet(), "No saved accounts!\nClick 'Add' to get started!")		;
	}

	private void loadAccounts() {
		viewPane.getChildren()
				.clear();
		showCenterMessage("Loading...");
		try {
			var accounts = accountService.fetchAccounts(SortColumn.DATE_ADDED);
			renderAccounts(accounts, "No saved accounts!\nClick 'Add' to get started!");
		} catch (StashException e) {
			showBottomMessage("Loading failed!");
			logger.severe(e::getMessage);
		} catch (CorruptDataException e) {
			handleCorruptDb(e);
		}
	}

	private void renderAccounts(Collection<Account> accounts, String emptyAccountsMessage) {
		if (accounts.isEmpty()) {
			showCenterMessage(emptyAccountsMessage);
		} else {
			clearCenterMessage();
			accounts.stream()
					.map(this::renderAccountCard)
					.forEach(accountCard -> accountCards.put(accountCard.getAccount(), accountCard));
		}
	}

	private AccountCard renderAccountCard(Account account) {
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
		updateMenuItem.setOnAction(event -> showUpdateModal(new SaveAccountModal(), account));

		MenuItem deleteMenuItem = new MenuItem("Delete");
		menuItems.add(deleteMenuItem);
		deleteMenuItem.setOnAction(event -> showDeleteModal(new DeleteAccountModal(), account));

		pane.setUserData(account);

		var insertIndex = calculateInsertIndex(account);
		viewPane.getChildren()
				.add(insertIndex, pane);

		return new AccountCard(pane, account);
	}

	private AccountCard renderAccountCard(AccountCard accountCard) {
		var createdAccountCard = renderAccountCard(accountCard.getAccount());
		accountCard.setCard(createdAccountCard.getCard());
		return accountCard;
	}

	private void updateAccountCard(Account account) {
		var accountCard = accountCards.get(account);
		var card = accountCard.getCard();

		var cardContents = card.getChildren();

		var domainLabel = (Label) cardContents.get(0);
		domainLabel.setText(account.getDomain());

		var usernameLabel = (Label) cardContents.get(1);
		usernameLabel.setText(account.getUsername());
	}

	private void removeAccountCard(Account account) {
		var accountCard = accountCards.get(account);
		var card = accountCard.getCard();

		viewPane.getChildren()
				.remove(card);
		accountCards.remove(account);
	}

	private void addCssClass(Node node, String cssClass) {
		List<String> classes = node.getStyleClass();
		classes.add(cssClass);
	}

	private int calculateInsertIndex(Account account) {
		var childNodes = viewPane.getChildren();

		if (childNodes.isEmpty()) {
			return 0;
		}

		var lowerBound = 0;
		var upperBound = childNodes.size() - 1;
		int index;

		for (;;) {
			index = (upperBound + lowerBound) / 2;

			var sampleAccount = (Account) childNodes.get(index).getUserData();
			var compare = activeComparator
					.compare(sampleAccount, account);
			if (0 == compare) {
				return index;
			} else if (0 > compare) {
				lowerBound = index + 1;
				if (lowerBound > upperBound) {
					return index + 1;
				}
			} else {
				upperBound = index - 1;
				if (lowerBound > upperBound) {
					return index;
				}
			}
		}
	}

	private <T> void showModal(AbstractTilePaneModal<T> modal, T data) {
		List<Node> rootChildren = rootPane.getChildren();
		rootChildren.add(modal);
		UiCommons.assignAnchors(modal, 0.0, 0.0, 0.0, 0.0);

		modal.setOnHandleCorruptDb(this::handleCorruptDb);
		modal.showModal(data);
	}

	private void showUpdateModal(SaveAccountModal modal, Account account) {
		modal.setOnClose(this::updateAccountCard);
		showModal(modal, account);
	}

	private void showDeleteModal(DeleteAccountModal modal, Account account) {
		modal.setOnClose(r -> removeAccountCard(account));
		showModal(modal, account);
	}

	private void showLoginModal(boolean startup) {
		try {
			boolean pinExists = authService.initializePin();

			var loginModal = new LoginModal();
			var flag = LoginFlag.REGULAR;

			if (startup) {
				loginModal.setOnClose(r -> {
					loadAccounts();
					initializeIdleMonitor();
				});
				if (!pinExists) {
					flag = LoginFlag.FIRST_TIME;
				}
			} else {
				idleMonitor.stopMonitoring();
				loginModal.setOnClose(r -> idleMonitor.startMonitoring());
				flag = LoginFlag.SESSION_EXPIRE;
			}

			showModal(loginModal, flag);
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

	private boolean matchesDomainOrUsername(Account account, String string) {
		var search = string.toLowerCase();
		var match = account.getDomain().toLowerCase()
				.contains(search) ||
			account.getUsername().toLowerCase()
				.contains(search);
		return match;
	}

	private void handleCorruptDb(CorruptDataException e) {
		showModal(new ResetModal(), null);
		logger.severe(e::getMessage);
	}
}
