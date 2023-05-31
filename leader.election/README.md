# Apache ZooKeeper

Apache ZooKeeper is an open-source server that is used for maintaining configuration information, naming, providing distributed synchronization, and offering group services in distributed applications. It helps to enable highly reliable distributed coordination by providing a distributed synchronization service and a registry for distributed applications.

## Key Functionalities

- **Leader Election:** ZooKeeper can assist distributed systems in performing leader election. Leader election is the process of designating a single process as the organizer of some task distributed among several nodes (computers).

- **Configuration Management:** ZooKeeper can store and manage configuration information across the nodes in the distributed system. Any changes made to the configuration data will be promptly propagated to all the nodes in the system.

- **Synchronization:** It provides a synchronization mechanism that allows nodes in a distributed environment to synchronize with each other.

- **Naming Service:** It allows one node to publish a piece of data under a name and other nodes to look up data by name.

- **Cluster Management:** ZooKeeper keeps track of which nodes are in the cluster and their status, thus enabling cluster management.

ZooKeeper guarantees that if we send a sequence of updates to a single ZooKeeper server, then all of those updates are applied in the order that they were sent. This makes it easier to coordinate tasks across a distributed system. It also has built-in mechanisms for dealing with failures, making it a robust solution for distributed coordination.

## Implementation details of Leader Election Algorithm
ZooKeeper provides an abstraction known as ephemeral sequential nodes. An ephemeral node is a type of znode (the data nodes ZooKeeper maintains in its data tree) which is deleted when the client session that created it ends. A sequential node is a type of znode that has an auto-incrementing integer appended to its name. We can use these two concepts to implement leader election.

To perform leader election, each client creates an ephemeral sequential node. The client with the node with the smallest appended sequence number assumes leadership. If the leading client fails, its ephemeral node will be removed by ZooKeeper, and the client with the next smallest sequence number can take over leadership.

For full implementation with step-by-step comments using the ZooKeeper API see LeaderElection file. The program first connects to a ZooKeeper server running on localhost. It creates an ephemeral sequential znode in the /election namespace. It then lists all znodes in the /election namespace, and if its znode has the smallest sequence number, it declares itself as the leader. If the leader goes offline, the ephemeral node will be deleted, and the remaining nodes will participate in a new round of leader election.

## Requirements
- Java 17
- Maven
- Apache ZooKeeper 3.8.1

## How to compile and run
1. Compile the project using Maven:
   To compile the project, navigate to the root directory of the project('leader.election') and run the following command:
```
mvn clean package
```
This command will compile the project and package the compiled code into a JAR file, which will be stored in the target directory.

2. Run the project:
   To run the project, navigate to the target directory and run the following command:
```
java -jar leader.election-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## How to test
To test the Leader Election implementation, we should run multiple instances of the application. Here's how to do it:
1. Start the ZooKeeper server:
   To start the ZooKeeper server, navigate to the bin directory of the ZooKeeper installation directory and run the following command:
```
zkServer.cmd
```
2. Open multiple terminals: We'll need to open multiple terminal windows or tabs, each representing a node in our distributed system.
3. Run the application on each node: In each terminal, navigate to the target directory as described above, and start the application using the java -jar command. Each instance of the application will act as a node in our distributed system.
4. Observe leader election: As each node comes online, watch the console output to see the leader election process in action. When the current leader goes offline, you should see a new leader being elected.

## References
- [Apache ZooKeeper](https://zookeeper.apache.org/)