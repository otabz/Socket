package com.extreme.xc;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketPooler extends AbstractSocketPooler {

	private static Logger log = Logger.getLogger(SocketPooler.class.getName());
	private static AtomicLong processNo = new AtomicLong(0);
	private final static ObjectPool<DigicoSocket> pool = new ObjectPool<DigicoSocket>(
			1, 1, 10) {

		@Override
		protected DigicoSocket createObject() {
			return new DigicoSocket(processNo.incrementAndGet());
		}

		@Override
		protected void destroyObject(DigicoSocket object) {
			try {
				object.kill();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	private static class SingletonHelper {
		private static final SocketPooler INSTANCE = new SocketPooler();
	}

	public static SocketPooler getInstance() {
		return SingletonHelper.INSTANCE;
	}

	public static void shutdown() throws InterruptedException {
		pool.shutdown();
	}

	private SocketPooler() {
	}

	@Override
	public DigicoSocket borrowSocket() {
		return pool.borrowObject();
	}

	@Override
	public void returnSocket(DigicoSocket socket) {
		pool.returnObject(socket);
	}

	public void testObjectPool(final SocketPooler pool)
			throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(8);

		// execute 8 tasks in separate threads

		executor.execute(new ExportingTask(pool, 1));
		executor.execute(new ExportingTask(pool, 2));
		Thread.sleep(10000);
		executor.execute(new ExportingTask(pool, 3));
		executor.execute(new ExportingTask(pool, 4));
		executor.execute(new ExportingTask(pool, 5));
		executor.execute(new ExportingTask(pool, 6));
		executor.execute(new ExportingTask(pool, 7));
		executor.execute(new ExportingTask(pool, 8));

		executor.shutdown();
		// executoeFixed.shutdown();
		try {
			// executoeFixed.awaitTermination(60, TimeUnit.SECONDS);
			executor.awaitTermination(60, TimeUnit.SECONDS);
		} catch (InterruptedException e)

		{
			e.printStackTrace();
		}
	}

	public void tearDown() throws InterruptedException {
		pool.shutdown();
	}

	public static void main(String[] args) throws InterruptedException {
		SocketPooler pooler = new SocketPooler();
		pooler.testObjectPool(pooler);
		pooler.tearDown();
	}
}
