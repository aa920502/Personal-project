default: BingTest.class
BingTest.class: src/BingTest.java

	javac -sourcepath src/ -classpath lib/commons-codec-1.7.jar  -d bin/ src/*.java


# If the first argument is "run"...
ifeq (run,$(firstword $(MAKECMDGOALS)))
  # use the rest as arguments for "run"
  RUN_ARGS := $(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS))
  # ...and turn them into do-nothing targets
  $(eval $(RUN_ARGS):;@:)
endif



run:
	#java -classpath bin/:lib/commons-codec-1.7.jar	BingTest
	java -classpath bin/:lib/commons-codec-1.7.jar	BingTest $(RUN_ARGS)	

