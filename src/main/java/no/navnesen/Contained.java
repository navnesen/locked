package no.navnesen;

public class Contained<T> implements AutoCloseable {

	private Key _key;

	public T value;

	Contained(Key key, T value) {
		this.value = value;
		this._key = key;
	}

	@Override
	public void close() {
		this.value = null;
		if (this._key != null) {
			Key key = this._key;
			this._key = null;
			key.release();
		}
	}
}
