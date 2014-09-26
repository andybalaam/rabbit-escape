
VERSION=0.1

CLASSPATH=rabbit-escape-engine/bin/:rabbit-escape-render/bin/:rabbit-escape-ui-text/bin/:rabbit-escape-ui-swing/bin/

IMAGES32_DEST=rabbit-escape-ui-swing/src/rabbitescape/ui/swing/images32

IMAGESSRC := $(wildcard images-src/*.svg)
IMAGES32 := $(IMAGESSRC:images-src/%.svg=$(IMAGES32_DEST)/%.png)

ANIMATIONS_DIR := rabbit-escape-render/src/rabbitescape/render/animations
LEVELS_DIRS := $(wildcard rabbit-escape-engine/src/rabbitescape/levels/*) \
               $(wildcard rabbit-escape-engine/test/rabbitescape/levels/*)

$(IMAGES32_DEST)/%.png: images-src/%.svg
	mkdir -p $(IMAGES32_DEST); inkscape $< --export-png=$@

all: compile

jar-generic: compile
	mkdir -p dist
	rm -f dist/rabbit-escape-generic-$(VERSION).jar
	cd rabbit-escape-engine/bin; \
		jar -cf ../../dist/rabbit-escape-generic-$(VERSION).jar `find ./`
	cd rabbit-escape-render/bin; \
		jar -uf ../../dist/rabbit-escape-generic-$(VERSION).jar `find ./`

images: $(IMAGES32)

%/ls.txt: %/*.re*
	ls $(@D) --hide=ls.txt > $(@D)/ls.txt

animations: $(ANIMATIONS_DIR)/ls.txt

levels: $(patsubst %, %/ls.txt, $(LEVELS_DIRS))

compile: images animations levels
	ant compile

clean:
	rm -rf \
		rabbit-escape-engine/bin/* \
		rabbit-escape-render/bin/* \
		rabbit-escape-ui-text/bin/* \
		rabbit-escape-ui-swing/bin/*
	find ./ -name "ls.txt" -delete
	rm -r dist

clean-images:
	rm -rf $(IMAGES32_DEST)/*

clean-all: clean clean-images

run: compile
	java -cp $(CLASSPATH) rabbitescape.ui.text.TextSingleGameMain test/level_01.rel

runinteractive: compile
	java -cp $(CLASSPATH) rabbitescape.ui.text.TextSingleGameMain test/level_01.rel --interactive

runmenu: compile
	java -cp $(CLASSPATH) rabbitescape.ui.text.TextMain

rungui: compile
	java -cp $(CLASSPATH) rabbitescape.ui.swing.SwingMain

runlevel: compile
	java -cp $(CLASSPATH) rabbitescape.ui.swing.SwingSingleGameMain test/level_01.rel

runat: compile
	java -cp $(CLASSPATH) rabbitescape.ui.swing.AnimationTester

test: compile
	# Work around what looks like an Ant 1.9 bug by including the classpath here
	CLASSPATH=lib/org.hamcrest.core_1.3.0.jar:lib/junit.jar ant test


