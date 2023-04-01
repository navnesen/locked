package no.navnesen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Locked {
	protected final AtomicReference<List<Task<Void>>> _queue = new AtomicReference<>(new ArrayList<>());
	protected final AtomicBoolean _isLocked = new AtomicBoolean(false);

	public Task<Lock> lock() {
		return new Task<>(() -> {
			this.waitForLock();
			return new Lock(this);
		});
	}

	protected void release() {
		if (this._queue.get().size() == 0) {
			this._isLocked.set(false);
		} else {
			this._queue.get().remove(0).completed(null);
		}
	}

	protected Task<Void> waitForLock() {
		Task<Void> task = new Task<>();
		if (this._isLocked.get()) {
			this._queue.get().add(task);
		} else {
			this._isLocked.set(true);
			task.completed(null);
		}
		return task;
	}
}
