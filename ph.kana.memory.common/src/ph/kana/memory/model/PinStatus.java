package ph.kana.memory.model;

public enum PinStatus {
	/**
	 * Initial state. No Pin file and no stash records.
	 */
	NEW,

	/**
	 * Pin file was created and there is at least a stash record.
	 */
	EXISTING,

	/**
	 * Pin file was created but missing and there is at least a stash record.
	 */
	MISSING
}
