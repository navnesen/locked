package no.navnesen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Lock {
	protected final AtomicReference<List<KeyWaiter>> _waiters = new AtomicReference<>(new ArrayList<>());
	protected final AtomicReference<Key> _activeKey = new AtomicReference<>(null);

	public boolean isLocked() {
		return this._activeKey.get() != null;
	}

	/**
	 * Seal the lock.
	 *
	 * @return A key waiter. Can be used to wait for the lock to become
	 * available.
	 */
	public KeyWaiter seal() {
		KeyWaiter waiter = new KeyWaiter(this);
		this._activeKey.updateAndGet(activeKey -> {
			if (activeKey == null) {
				Key key = new Key(this);
				waiter._lock.set(null);
				waiter.completed(key);
				return key;
			} else {
				this._waiters.getAndUpdate(waiters -> {
					waiters.add(waiter);
					return waiters;
				});
				return activeKey;
			}
		});
		return waiter;
	}

	protected void release(Key key) {
		if (key != this._activeKey.get()) {
			throw new RuntimeException("Keys doesn't match!");
		}

		KeyWaiter waiter = null;

		if (this._waiters.get().size() > 0) {
			waiter = this._waiters.get().remove(0);
		}

		if (waiter != null) {
			Key newKey = new Key(this);
			this._activeKey.set(newKey);
			waiter._lock.set(null);
			waiter.completed(newKey);
		} else {
			this._activeKey.set(null);
		}
	}

}
