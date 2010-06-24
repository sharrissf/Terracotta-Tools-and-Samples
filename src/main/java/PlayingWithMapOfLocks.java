import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;

import org.terracotta.api.ClusteringToolkit;
import org.terracotta.api.TerracottaClient;
import org.terracotta.collections.ClusteredMap;
import org.terracotta.coordination.Barrier;

/**
 * 
 * @author steve
 * 
 *         Simple demonstration of using cyclic barrier and reentrant read-write
 *         locks striped over a hashmap
 * 
 *         Usage: PlayingWithMapOfLocks <numberOfNodes>
 * 
 */
public class PlayingWithMapOfLocks {

	public static void main(String[] args) {

		final String barrierName = args[0];
		final int numberOfParties = Integer.parseInt(args[1]);
		TerracottaClient client = new TerracottaClient("localhost:9510");
		ClusteringToolkit clustering = client.getToolkit();

		ClusteredMap<String, Object> map = clustering.getMap("MyMap");
		Barrier barrier = clustering.getBarrier(barrierName, numberOfParties);
		ReadWriteLock l = clustering.getReadWriteLock(clustering
				.getClusterInfo().getUniversallyUniqueClientID());
		map.put(clustering.getClusterInfo().getUniversallyUniqueClientID(), l);

		try {
			System.out.println("Waiting ...");
			int index = barrier.await();
			Set<String> keys = (Set<String>) map.keySet();
			for (String k : keys) {

				ReadWriteLock mrwl = (ReadWriteLock) map.get(k);

				System.out.println("MAP Locked: " + k + ": "
						+ mrwl.writeLock().tryLock(1, TimeUnit.SECONDS));
			}
			barrier.await();
			System.out.println("... finished " + index);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
