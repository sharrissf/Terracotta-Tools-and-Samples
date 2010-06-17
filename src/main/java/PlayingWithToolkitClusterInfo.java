import java.util.Random;

import org.terracotta.api.ClusteringToolkit;
import org.terracotta.api.TerracottaClient;
import org.terracotta.cluster.ClusterEvent;
import org.terracotta.cluster.ClusterInfo;
import org.terracotta.cluster.ClusterListener;
import org.terracotta.cluster.ClusterTopology;

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

			public void operationsDisabled(ClusterEvent arg0) {

			}

			public void operationsEnabled(ClusterEvent arg0) {

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
