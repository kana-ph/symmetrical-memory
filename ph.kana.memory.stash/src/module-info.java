module ph.kana.memory.stash {
	requires java.sql;

	requires ph.kana.memory.common;
	requires derby;
	requires sqljet;
	requires zip4j;
	requires jbcrypt;

	exports ph.kana.memory.stash;
}
