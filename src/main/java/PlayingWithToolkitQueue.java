import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

import org.terracotta.api.ClusteringToolkit;
import org.terracotta.api.TerracottaClient;
import org.terracotta.collections.ClusteredMap;
import org.terracotta.coordination.Barrier;

/**
 * Simple example of using a blocking queue in a cluster
 * 
 * @author steve
 * 
 *         Usage: start two of these and watch the putter (chosen at random) put
 *         and the getter get off the queue
 * 
 */
public class PlayingWithToolkitQueue {
	public static void main(String[] args) throws Exception {
		ClusteringToolkit clustering = new TerracottaClient("localhost:9510")
				.getToolkit();

		Barrier barrier = clustering.getBarrier("queueBarrier", 2);

		BlockingQueue<byte[]> expressQueue = clustering
				.getBlockingQueue("myQueue");

		ClusteredMap<String, BlockingQueue<byte[]>> map = clustering
				.getMap("myMap");
		boolean putter = map.put("myQueue", expressQueue) == null;
		System.out.println("waiting");
		barrier.await();

		BlockingQueue<byte[]> bq = map.get("myQueue");

		if (putter) {

			MyObject mo = new MyObject("Hello, Steve");
			bq.put(serializeMyObject(mo));
		}
		if (!putter) {

			System.out.println("Got: " + deserializeMyObject(bq.take())
					+ " putter: " + putter);
		} else {
			System.out.println("Not the taker");
		}
	}

	private static byte[] serializeMyObject(MyObject mo) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(mo);
		return baos.toByteArray();
	}

	private static MyObject deserializeMyObject(byte[] take) {
		try {
			return (MyObject) new ObjectInputStream(new ByteArrayInputStream(
					take)).readObject();
		} catch (IOException e) {
			throw new AssertionError(e);
		} catch (ClassNotFoundException e) {
			throw new AssertionError(e);
		}
	}

	static class MyObject implements Serializable {
		private final String message;

		public MyObject(String message) {
			this.message = message;
		}

		public String toString() {
			return this.message;
		}
	}
}
