
CLASSPATH=rabbit-escape-engine/bin/:rabbit-escape-render/bin/:rabbit-escape-ui-text/bin/:rabbit-escape-ui-swing/bin/

IMAGES32_DEST=rabbit-escape-ui-swing/src/rabbitescape/ui/swing/images32

IMAGESSRC := $(wildcard images-src/*.svg)
IMAGES32 := $(IMAGESSRC:images-src/%.svg=$(IMAGES32_DEST)/%.png)

ANIMATIONS_DIR := rabbit-escape-render/src/rabbitescape/render/animations
LEVELS_DIRS := $(wildcard rabbit-escape-engine/src/rabbitescape/levels/*)

$(IMAGES32_DEST)/%.png: images-src/%.svg
	mkdir -p $(IMAGES32_DEST); inkscape $< --export-png=$@

all: compile

images: $(IMAGES32)

%/ls.txt: %/*.re*
	ls $(@D) --hide=ls.txt > $(@D)/ls.txt

animations: $(ANIMATIONS_DIR)/ls.txt

levels: $(patsubst %, %/ls.txt, $(LEVELS_DIRS))

compile: images animations
	ant compile

clean:
	rm -rf \
		rabbit-escape-engine/bin/* \
		rabbit-escape-render/bin/* \
		rabbit-escape-ui-text/bin/* \
		rabbit-escape-ui-swing/bin/*
	find ./ -name "ls.txt" -delete

clean-images:
	rm -rf $(IMAGES32_DEST)/*

clean-all: clean clean-images

run: compile
	java -cp $(CLASSPATH) rabbitescape.ui.text.TextMain test/level_01.rel

runinteractive: compile
	java -cp $(CLASSPATH) rabbitescape.ui.text.TextMain test/level_01.rel --interactive

runmenu: compile
	java -cp $(CLASSPATH) rabbitescape.ui.text.TextMenuMain

rungui: compile
	java -cp $(CLASSPATH) rabbitescape.ui.swing.SwingMain test/level_03.rel

runat: compile
	java -cp $(CLASSPATH) rabbitescape.ui.swing.AnimationTester

test: compile
	# Work around what looks like an Ant 1.9 bug by including the classpath here
	CLASSPATH=lib/org.hamcrest.core_1.3.0.jar:lib/junit.jar ant test


