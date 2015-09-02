Brief description

In this project, I used UDP protocol to send data from sender to receiver, and I used TCP to send ACK from receiver back to sender. The program provides packet loss recovery mechanism to simulate TCP reliable transmission.
----------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------
In order to run this program, 
first do the compilation: run Makefile (Makefile is only for compilation)

Without Proxy
At sender end:  java Sender test_file.txt 127.0.0.1 20002 20001 stdout.txt 1
At receiver end: java Receiver receivedData.txt 20002 127.0.0.1 20001 stdout.txt

With Proxy (1000 ---> 10000 ----> 5555)
At sender end: java Sender test_file.txt 127.0.0.1 1000 20001 stdout.txt 1
At receiver end: java Receiver receivedData.txt 5555 127.0.0.1 20001 stdout.txt

Note: you need to define the proxy port information correctly to run sender and receiver

--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
a) TCP segment structure
  It has following fields:

  short portNumber;
  short destinationPort;
  int sequenceNumber;
  int acknowledgementNumber;
  boolean FIN;
  boolean ACK;
  short window;
  short checksum;
	
In this program I also wrote several functions in order to calculate FIN/ACK boolean value based upon the input bytes and checksum by adding up all the bytes in a byte array and reverse them. I omitted the last unused two bytes after the check sum for the integrity of checksum.

b) States typically visited by a sender and receiver
In sender I have a boolean value TCPDone which is set to False by default, this variable indicates whether TCP connection has been started or finished at sender side. While TCPDone is false, it means sender is still in the state of sending data, and sender will be receiving header information from receiver using TCP at the same time. Once the sender detects that the ACK number in receiver-sending-header is equal to the file length plus one, it means that last packet is received, therefore TCP session is done. At sender side, if sender sees that currently received ACK number is greater than the file length, it will also generate a FIN bit and send it to receiver in order to tell it to end the connection.

In receiver it also has a boolean value TCPDone which is set to False by default, while the value is false, it will keep receiving data from sender using TCP while sending back updated header data at the same time. TCPDone will be set to true as soon as receiver receives a segment which FIN bit is set to true in its header.

c)Loss recovery mechanism
In loss recovery mechanism, I implemented checksum, timer and RTT checker. Every time sender sends data, data will be packed with a corresponding header, which includes a checksum. This checksum will be checked again once the packet arrives at receiver, if checksum is not correct, receiver will send back same ACK number, when sender receives the ACK and see that the number is not changed, it knows that packet is not correctly received and it will retransmit; if the packet is not corrupted, it will send back (ACK+1) to sender to let sender know that it is expecting next packet. 
I set three numbers at sender side to keep track of packets sent: base packet number, current packet number. Once the transmission at sender end begins, timer will also begin to count. If there is a timeout, we move current packet pointer back to base packet number and clear the timeout, this will make sender to retransmit the last lost packet. 
RTT manager is a class I implemented to update timeout and calculate round trip time. It has an ordered set structure which contains data with corresponding sequence number. At sender side, every time receiving an ACK, we will use this ACK number to calculate estimated RTT time and estimated timeout interval. All calculation formulas and estimated values are obtained from book.

