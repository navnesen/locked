package no.navnesen;

public class Mutex<T> {

	private T _value;
	private Lock _lock;

	Mutex(T value) {
		this._value = value;
	}

	public T unsafeGet() {
		return this._value;
	}

	public void unsafeSet(T value) {
		this._value = value;
	}

	public Contained<T> lock() {
		return new Contained<>(this._lock.seal().await(), this);
	}

	public void set(T value) {
		var key = this._lock.seal().await();
		this._value = value;
		key.release();
	}


}
