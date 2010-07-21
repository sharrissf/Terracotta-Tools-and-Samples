import org.terracotta.api.ClusteringToolkit;
import org.terracotta.api.TerracottaClient;
import org.terracotta.util.ClusteredAtomicLong;
/**
 * This is a very basic sample that increments an atomic long. Run a couple hundred of these
 * at a time and see that no number is duplicated
 * @author steve
 *
 */
public class PlayingWithToolkitClusterCounter {
	public static void main(String[] args) {

		// Start the Terracotta client
		ClusteringToolkit toolkit = new TerracottaClient("localhost:9510")
				.getToolkit();

		// Get an instance of a counter by name
		ClusteredAtomicLong atomicLong = toolkit.getAtomicLong("MySharedLong");
		System.out.println("Client: "
				+ toolkit.getClusterInfo().getUniversallyUniqueClientID()
				+ " is incrementing the long to: "
				+ atomicLong.getAndIncrement());
	}
}
