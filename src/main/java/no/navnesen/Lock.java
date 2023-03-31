package no.navnesen;

import java.util.concurrent.atomic.AtomicReference;

import static no.navnesen.Raise.raise;

public class Lock {
	private final AtomicReference<Locked> locked;

	protected Lock(Locked locked) {
		this.locked = new AtomicReference<>(locked);
	}

	public void unlock() {
		var locked = this.locked.getAndSet(null);
		if (locked == null) raise(new LockedException());
		locked.release();
	}
}
