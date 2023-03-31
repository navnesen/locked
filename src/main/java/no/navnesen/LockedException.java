package no.navnesen;

public class LockedException extends Exception {
	public LockedException() {
		super("Locked!");
	}
}
