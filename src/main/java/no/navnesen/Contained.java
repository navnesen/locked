package no.navnesen;

public class Contained<T> implements AutoCloseable {

	private Key _key;
	private Mutex<T> _mutex;

	Contained(Key key, Mutex<T> mutex) {
		this._mutex = mutex;
		this._key = key;
	}

	public T get() {
		return this._mutex.unsafeGet();
	}

	public void set(T value) {
		this._mutex.unsafeSet(value);
	}

	@Override
	public void close() {
		this._mutex = null;
		if (this._key != null) {
			Key key = this._key;
			this._key = null;
			key.release();
		}
	}
}
