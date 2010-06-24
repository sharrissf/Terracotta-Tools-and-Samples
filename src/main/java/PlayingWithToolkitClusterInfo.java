import java.util.Random;

import org.terracotta.api.ClusteringToolkit;
import org.terracotta.api.TerracottaClient;
import org.terracotta.cluster.ClusterEvent;
import org.terracotta.cluster.ClusterInfo;
import org.terracotta.cluster.ClusterListener;
import org.terracotta.cluster.ClusterTopology;

/**
 * Prints out the 4 main cluster events that occur as they happen. Start and
 * stop many nodes and watch the printouts as nodes join and leave. You can also
 * see what happens when you start and stop servers (put the server in permanent
 * store mode to be able to restart it) and see operations enable and disable.
 * 
 * @author steve
 * 
 */
public class PlayingWithToolkitClusterInfo {

	public static void main(String[] args) throws Exception {

		// Start the Terracotta client
		ClusteringToolkit toolkit = new TerracottaClient("localhost:9510")
				.getToolkit();
		ClusterInfo clusterInfo = toolkit.getClusterInfo();
		clusterInfo.addClusterListener(new ClusterListener() {

			public void nodeJoined(ClusterEvent event) {
				System.out.println("Node Joined: " + event.getNode().getId());
			}

			public void nodeLeft(ClusterEvent event) {
				System.out.println("Node Left: " + event.getNode().getId());
			}

			public void operationsDisabled(ClusterEvent event) {
				System.out.println("Operations Disabled: "
						+ event.getNode().getId());
			}

			public void operationsEnabled(ClusterEvent event) {
				System.out.println("Operations Enabled: "
						+ event.getNode().getId());
			}

		});

		ClusterTopology topology = clusterInfo.getClusterTopology();
		for (int count = 0; count < 5; count++) {
			Thread.sleep(2000 + new Random(System.currentTimeMillis())
					.nextInt(5000));
			System.out.println("I am: "
					+ clusterInfo.getUniversallyUniqueClientID()
					+ " Topology: " + topology.getNodes().size());
		}

	}
}
