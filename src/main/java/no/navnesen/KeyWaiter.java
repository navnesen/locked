package no.navnesen;

import java.util.concurrent.atomic.AtomicReference;

public class KeyWaiter extends Task<Key> {
	protected AtomicReference<Lock> _lock;

	public KeyWaiter(Lock lock) {
		super();
		this._lock = new AtomicReference<>(lock);
	}

	public void cancel() {
		if (this._lock.get() == null) {
			throw new RuntimeException("Cannot cancel! Currently not waiting for key!");
		}
		this._lock.getAndSet(null)._waiters.getAndUpdate(waiters -> {
			waiters.remove(this);
			this.failed(new CancelledException());
			return waiters;
		});
	}
}
