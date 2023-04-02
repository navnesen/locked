package no.navnesen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class LockTest {
	@Test
	public void verifyInitialLockState() {
		Lock lock = new Lock();
		assert lock._activeKey.get() == null;
		assert lock._waiters.get().size() == 0;
	}

	@Test
	public void verifyWaiterCompletedWhenLockQueueIsEmpty() {
		Lock lock = new Lock();
		KeyWaiter waiter = lock.seal();
		assert waiter != null;
		assert waiter._result.get() != null;
		assert waiter._result.get().value != null;
		assert lock._activeKey.get() == waiter._result.get().value;
		assert lock.isLocked();
	}

	@Test
	public void verifyWaiterAwaits() {
		assert new Lock().seal().await() != null;
	}

	@Test
	public void verifyWaiterIsAddedToQueue() {
		Lock lock = new Lock();
		Key key = lock.seal().await();
		KeyWaiter waiter = lock.seal();
		assert lock._activeKey.get() == key;
		assert lock._waiters.get().size() > 0;
	}

	@Test
	public void verifyReleases() {
		Lock lock = new Lock();
		lock.seal().await().release();
		assert lock._activeKey.get() == null;
		assert lock._waiters.get().size() == 0;
	}

	@Test
	public void verifyLockDoesNotReleaseOnKeyMismatch() {
		assertThrows(RuntimeException.class, () -> {
			Lock lock = new Lock();
			lock.seal().await();
			lock.release(new Key(lock));
		});
	}

	@Test
	public void verifyReleasesToNext() {
		Lock lock = new Lock();
		KeyWaiter waiter1 = lock.seal();
		KeyWaiter waiter2 = lock.seal();
		waiter1._result.get().value.release();
		assert waiter2._result.get() != null;
		assert waiter2._result.get().value != null;
	}

	@Test
	public void verifyWaiterCancels() {
		Lock lock = new Lock();
		Key key = lock.seal().await();
		KeyWaiter waiter = lock.seal();
		waiter.cancel();
		assertThrows(CancelledException.class, waiter::await);
		assert lock._activeKey.get() == key;
		assert lock._waiters.get().size() == 0;
	}

	@Test
	public void verifyWaiterCannotCancelTwice() {
		Lock lock = new Lock();
		lock.seal();
		KeyWaiter waiter = lock.seal();
		waiter.cancel();
		assertThrows(RuntimeException.class, waiter::cancel);
	}

	@Test
	public void verifyKeyCannotReleaseTwice() {
		Key key = new Lock().seal().await();
		key.release();
		assertThrows(RuntimeException.class, key::release);
	}
}