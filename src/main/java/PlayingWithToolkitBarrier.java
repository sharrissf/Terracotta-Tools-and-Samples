import org.terracotta.api.ClusteringToolkit;
import org.terracotta.api.TerracottaClient;
import org.terracotta.coordination.Barrier;

public class PlayingWithToolkitBarrier {

	public static void main(String[] args) {
		final String barrierName = args[0];
		final int numberOfParties = Integer.parseInt(args[1]);

		// Start the Terracotta client
		ClusteringToolkit clustering = new TerracottaClient("localhost:9510")
				.getToolkit();

		// Get an instance of a barrier by name
		Barrier barrier = clustering.getBarrier(barrierName, numberOfParties);
		try {
			System.out.println("Waiting ...");
			int index = barrier.await();
			System.out.println("... finished " + index);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}