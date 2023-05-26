import org.apache.zookeeper.*;
import java.util.Collections;
import java.util.List;

public class LeaderElection implements Watcher {

    // Define the Zookeeper server, session timeout, and election znode path
    private static final String ZOOKEEPER_ADDRESS = "localhost:2181";
    private static final int SESSION_TIMEOUT = 3000;
    private static final String ELECTION_NAMESPACE = "/election";

    // currentZnodeName represents the name of the znode created by this instance
    private String currentZnodeName;

    // ZooKeeper instance which manages the connection and communication with the Zookeeper server.
    private ZooKeeper zooKeeper;

    public static void main(String[] args) throws Exception {
        LeaderElection leaderElection = new LeaderElection();

        // Connect to the ZooKeeper server
        leaderElection.connectToZookeeper();

        // Create an ephemeral sequential ZNode
        leaderElection.volunteerForLeadership();

        // Perform leader election
        leaderElection.electLeader();

        // Wait (keep this process alive)
        leaderElection.run();
        leaderElection.close();
    }

    // Method to connect to the Zookeeper server
    public void connectToZookeeper() throws Exception {
        this.zooKeeper = new ZooKeeper(ZOOKEEPER_ADDRESS, SESSION_TIMEOUT, this);
    }

    // Method to volunteer for leadership
    public void volunteerForLeadership() throws Exception {
        // Define the znode name prefix
        String znodeFullPath = ELECTION_NAMESPACE + "/c_";

        // Create an ephemeral sequential znode
        String createdZnodeName = zooKeeper.create(znodeFullPath, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        // Store the znode name of the created node (without the namespace prefix)
        this.currentZnodeName = createdZnodeName.replace(ELECTION_NAMESPACE + "/", "");
    }

    // Method to perform leader election
    public void electLeader() throws Exception {
        // Get a list of all children znodes in the election namespace
        List<String> children = zooKeeper.getChildren(ELECTION_NAMESPACE, false);

        // Sort the list of children znodes
        Collections.sort(children);

        // The smallest znode in the sorted list
        String smallestChild = children.get(0);

        // If the current znode is the smallest one, this client is the leader
        if (smallestChild.equals(currentZnodeName)) {
            System.out.println("I am the leader");
            return;
        }

        System.out.println("I am not the leader, " + smallestChild + " is the leader");
    }

    // Method to keep the application running until the ZooKeeper session ends
    public void run() throws InterruptedException {
        synchronized (zooKeeper) {
            zooKeeper.wait();
        }
    }

    // Method to close the ZooKeeper connection
    public void close() throws InterruptedException {
        zooKeeper.close();
    }

    // Overriding the process method of the Watcher interface
    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case None:
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("Successfully connected to ZooKeeper");
                } else {
                    synchronized (zooKeeper) {
                        System.out.println("Disconnected from ZooKeeper event");
                        zooKeeper.notifyAll();
                    }
                }
        }
    }
}