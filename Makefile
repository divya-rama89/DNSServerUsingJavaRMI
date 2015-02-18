

JAVAC=javac
sources = $(wildcard Client.java Server.java DNSlookup.java DNSreply.java)
classes = $(sources:.java=.class)

all: $(classes)

clean :
	rm -f *.class

%.class : %.java
	$(JAVAC) $<
