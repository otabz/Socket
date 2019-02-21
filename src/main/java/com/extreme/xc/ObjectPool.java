package com.extreme.xc;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ObjectPool<T> {

	private static Logger log = Logger.getLogger(ObjectPool.class.getName());
	private ConcurrentLinkedQueue<T> pool;
	private ScheduledExecutorService executorService;

	public ObjectPool(final int minObjects) {
		// initialize pool
		initialize(minObjects);

	}

	public ObjectPool(final int minObjects, final int maxObjects,
			final long validationInterval) {
		// initialize pool
		initialize(minObjects);

		executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				int size = pool.size();

				if (size < minObjects) {
					int sizeToBeAdded = minObjects + size;
					log.log(Level.INFO, "Watcher Thread: (less than minimum) min: "+minObjects+" size: "+size);
					for (int i = 0; i < sizeToBeAdded; i++) {
						pool.add(createObject());
					}
				} else if (size > maxObjects) {
					int sizeToBeRemoved = size - maxObjects;
					log.log(Level.INFO, "Watcher Thread: (greater than maximum) max: "+maxObjects+" size: "+size);
					for (int i = 0; i < sizeToBeRemoved; i++) {
						T object;
						if((object = pool.poll()) != null){
							destroyObject(object);
						}
					}
				}
			}
		}, validationInterval, validationInterval, TimeUnit.MINUTES);
		System.out.println("ObjectPool initialized . . .");
	}

	protected abstract T createObject();
	
	protected abstract void destroyObject(T object);

	public T borrowObject() {
		T object;
		if ((object = pool.poll()) == null) {
			object = createObject();
		}
		return object;
	}

	public void returnObject(T object) {
		if (object == null) {
			return;
		}
		this.pool.offer(object);
	}

	private void initialize(final int minObjects) {
		pool = new ConcurrentLinkedQueue<T>();
		for (int i = 0; i < minObjects; i++) {
			pool.add(createObject());
		}
	}
	
	 public void shutdown() throws InterruptedException{  
	        if (executorService != null){  
	        	 executorService.shutdown();
	        }
	    }  
}
