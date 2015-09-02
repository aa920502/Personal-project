README
Junchao Lu

1. Program features:

This program enables multiple client nodes to be setup on one or more machines. Each setup involves one or more connections between any pair of clients. “SHOWRT” command is implemented to show current routing table at current client, which should display all shortest paths to all reached clients. “LINKDOWN <IP PORT>” command enables current client to disconnect a neighbour client within its reach and cut only that path. “LINKUP <IP PORT> command enables current client to recover previously cut path”, at any time, we can use “SHOWRT” to check current route table at the client. In this program, you can link down multiple times and link up back all paths, the results remain same and optimal at each step.

2. Protocol specification of implementation:

Since in this program we used Bellman Ford algorithm, the core of this algorithm is:
Given current client A, if A can reach a client E, also A has a neighbour B, if B can also reach client E, we check the distance from B to E and A to B, if dist(A->E)>dist(A->B)+dist(B->E), we will update dist(A->E) to be dist(A->B)+dist(B->E). We will do such a procedural checking for each neighbour of A. After we finish, we’ll get current shortest path from A to E through either one of A’s neighbours.

The most important part of the algorithm is to calculate distance vectors and decide whether the table has been modified or not.If the table has been modified, the modifications will be sent to all the client’s neighbours; otherwise, nothing will be sent out since it assumes this is the current best result before any new information comes in. At each step, we save old distance vectors first, if there are unknown clients reachable, add those clients, then we clear all neighbour clients values and assign them with updated values using judgements based on previously mentioned bellman ford algorithm. In handling “CLOSE”, my program will do as following way:If A is connected with B, B is connected with C, A is closed. For B, in its RT,its distance to A will keep increasing until it becomes “null” (infinity).

Also, unexpected exit problem is also handled, pressing Ctrl+c will not cause the program to crash or throw any exception, it will have the same effect as running CLOSE command.

I have used three data messaged: “con” “recon” “distanceVector”
Syntax:  "command:con\n"+"port:"+portNumber+"\n"+"cost:"+cost+"\n"
Syntax:  "command:recon\n"+"port:"+portNumber+"\n"+"cost:0\n"
Syntax:  "command:distanceVector\n"+"port:"+portNumber+"\n"
If received message is “con”, it means there is a new connection, I record the new connected node, also I updated the client’s neighbour information list, then the updated information is sent to all neighbours.
If received message is “recon”, it means that the node is already connected, in this case, we just refresh the connection and the node’s failure time.
If received message is “distanceVector”, we will calculate and update the distance vectors based on Bellman Ford algorithm, if the table is modified after analyzing all those information together with previously stored information, then we will send this updated table to all the client’s neighbours; otherwise, we don’t do anything.


3. Example code to run the program:

make
(window1): java bfclient 4116 1
(window2): java bfclient 4118 1 209.2.209.137 4116 5.0
(window3): java bfclient 4115 1 209.2.209.137 4116 5.0 209.2.209.137 4118 30.0
(window4): java bfclient 4117 1 209.2.209.137 4116 10.0

This will form the topology displayed in the assignment, after that, you can type the commands to test the effect.