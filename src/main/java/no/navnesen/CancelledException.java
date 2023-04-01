package no.navnesen;

public class CancelledException extends Exception {
	public CancelledException() {
		super("Locking request was cancelled!");
	}
}
