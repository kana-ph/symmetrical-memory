package ph.kana.memory.ui.model;

import ph.kana.memory.model.Account;
import ph.kana.memory.type.SortColumn;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;

public final class AccountComparator {

	private final static Map<SortColumn, Comparator<Account>> COMPARATORS = new EnumMap<>(SortColumn.class);

	static {
		var dateComparator = Comparator.comparingLong(Account::getSaveTimestamp);
		var domainComparator = Comparator.comparing(Account::getDomain)
				.thenComparing(Account::getUsername)
				.thenComparing(dateComparator);
		var usernameComparator = Comparator.comparing(Account::getUsername)
				.thenComparing(Account::getDomain)
				.thenComparing(dateComparator);

		COMPARATORS.put(SortColumn.DATE_ADDED, dateComparator);
		COMPARATORS.put(SortColumn.DOMAIN, domainComparator);
		COMPARATORS.put(SortColumn.USERNAME, usernameComparator);
	}

	public static Comparator<Account> of(SortColumn sortColumn) {
		return COMPARATORS.get(sortColumn);
	}


	private AccountComparator() {}
}
