import java.util.Collection;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;

import org.terracotta.api.ClusteringToolkit;
import org.terracotta.api.TerracottaClient;
import org.terracotta.cluster.ClusterNode;
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
public class PlayingWithToolkitMapOfLocks {

	public static void main(String[] args) {

		final String barrierName = "mapOfLocksBarrier";

		final int numberOfParties = Integer.parseInt(args.length == 0 ? "4"
				: args[0]);
		TerracottaClient client = new TerracottaClient("localhost:9510");
		ClusteringToolkit clustering = client.getToolkit();

		ClusteredMap<String, Object> map = clustering.getMap("MyMap");
		Barrier barrier = clustering.getBarrier(barrierName, numberOfParties);
		ReadWriteLock l = clustering.getReadWriteLock(clustering
				.getClusterInfo().getCurrentNode().getId());
		map.put(clustering.getClusterInfo().getCurrentNode().getId(), l);

		try {
			System.out.println("Waiting ...");
			int index = barrier.await();
			Random r = new Random();
			Collection<ClusterNode> keys = clustering.getClusterInfo()
					.getClusterTopology().getNodes();
			for (ClusterNode k : keys) {
				Thread.sleep(1000 + r.nextInt(2000));
				ReadWriteLock mrwl = (ReadWriteLock) map.get(k.getId());
				boolean winner = mrwl.writeLock().tryLock(1,
						TimeUnit.MILLISECONDS);
				System.out.println("MAP Locked: " + k.getId() + ": " + winner);
				if (winner)
					break;
			}
			barrier.await();
			System.out.println("... finished " + index);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
