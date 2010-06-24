import org.terracotta.api.ClusteringToolkit;
import org.terracotta.api.TerracottaClient;
import org.terracotta.coordination.Barrier;

/**
 * 
 * @author steve Start up n names and they will wait until all n nodes hit the
 *         barrier and then continue
 * 
 *         Usage PlayingWithToolkitBarrier <nameOfBarrier> <numberOfNodes>
 */
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