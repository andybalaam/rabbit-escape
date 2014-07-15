
all: compile

compile:
	ant compile

clean:
	rm -rf rabbit-escape-engine/bin/* rabbit-escape-ui-text/bin/*

run: compile
	java -cp rabbit-escape-engine/bin/:rabbit-escape-ui-text/bin/ rabbitescape.ui.text.Main levels/basic/level_01.rel

test: compile
	# Work around what looks like an Ant 1.9 bug by including the classpath here
	CLASSPATH=../lib/org.hamcrest.core_1.3.0.jar:../lib/junit.jar ant test


