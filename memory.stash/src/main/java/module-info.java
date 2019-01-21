module ph.kana.memory.stash {
	requires java.sql;

	requires ph.kana.memory.common;
	requires derby;
	requires zip4j;
	requires jbcrypt;

	exports ph.kana.memory.account;
	exports ph.kana.memory.auth;
	exports ph.kana.memory.stash;
	exports ph.kana.memory.backup;
}
