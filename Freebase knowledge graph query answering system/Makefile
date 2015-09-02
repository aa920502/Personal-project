default: compile

compile: 
	javac -cp "./commons-logging-1.1.1.jar:./google-http-client-1.19.0.jar:./gson-2.1.jar:httpclient-4.0.1.jar:./httpcore-4.0.1.jar:jackson-core-2.1.3.jar:./jackson-core-asl-1.9.11.jar:./jetty-6.1.26.jar:./jetty-util-6.1.26.jar:./json-path-0.5.5.jar:./json-simple-1.1.jar:./json-smart-1.1.jar:./jsr305-1.3.9.jar:./protobuf-java-2.4.1.jar:./transaction-api-1.1.jar:./xpp3-1.1.4c.jar" *.java


# If the first argument is "run"...
ifeq (run,$(firstword $(MAKECMDGOALS)))
  # use the rest as arguments for "run"
  RUN_ARGS := $(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS))
  # ...and turn them into do-nothing targets
  $(eval $(RUN_ARGS):;@:)
endif



run:
	#java -cp "./commons-logging-1.1.1.jar:./google-http-client-1.19.0.jar:./gson-2.1.jar:httpclient-4.0.1.jar:./httpcore-4.0.1.jar:jackson-core-2.1.3.jar:./jackson-core-asl-1.9.11.jar:./jetty-6.1.26.jar:./jetty-util-6.1.26.jar:./json-path-0.5.5.jar:./json-simple-1.1.jar:./json-smart-1.1.jar:./jsr305-1.3.9.jar:./protobuf-java-2.4.1.jar:./transaction-api-1.1.jar:./xpp3-1.1.4c.jar" SearchExample
	java -cp :./commons-logging-1.1.1.jar:./google-http-client-1.19.0.jar:./gson-2.1.jar:httpclient-4.0.1.jar:./httpcore-4.0.1.jar:jackson-core-2.1.3.jar:./jackson-core-asl-1.9.11.jar:./jetty-6.1.26.jar:./jetty-util-6.1.26.jar:./json-path-0.5.5.jar:./json-simple-1.1.jar:./json-smart-1.1.jar:./jsr305-1.3.9.jar:./protobuf-java-2.4.1.jar:./transaction-api-1.1.jar:./xpp3-1.1.4c.jar SearchExample $(RUN_ARGS)
	#java -classpath bin/:lib/commons-codec-1.7.jar	BingTest $(RUN_ARGS)	

