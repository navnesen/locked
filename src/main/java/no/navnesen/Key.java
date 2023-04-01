package no.navnesen;

public class Key {
	private Lock _lock;

	public Key(Lock lock) {
		this._lock = lock;
	}

	public void release() {
		if (this._lock == null) {
			throw new RuntimeException("Key has already been released!");
		}
		Lock lock = this._lock;
		this._lock = null;
		lock.release(this);
	}
}
