
CLASSPATH=rabbit-escape-engine/bin/:rabbit-escape-render/bin/:rabbit-escape-ui-text/bin/:rabbit-escape-ui-swing/bin/

IMAGES32_DEST=rabbit-escape-ui-swing/src/rabbitescape/ui/swing/images32

IMAGESSRC := $(wildcard images-src/*.svg)
IMAGES32 := $(IMAGESSRC:images-src/%.svg=$(IMAGES32_DEST)/%.png)

ANIMATIONS_DIR := rabbit-escape-render/src/rabbitescape/render/animations
ANIMATIONS_LS := $(ANIMATIONS_DIR)/ls.txt

$(IMAGES32_DEST)/%.png: images-src/%.svg
	mkdir -p $(IMAGES32_DEST); inkscape $< --export-png=$@

all: compile

images: $(IMAGES32)

$(ANIMATIONS_LS): $(ANIMATIONS_DIR)/*.rea
	cd $(ANIMATIONS_DIR); ls *.rea > ls.txt

animations: $(ANIMATIONS_LS)

compile: images animations
	ant compile

clean:
	rm -rf \
		rabbit-escape-engine/bin/* \
		rabbit-escape-render/bin/* \
		rabbit-escape-ui-text/bin/* \
		rabbit-escape-ui-swing/bin/*

clean-all: clean
	rm -rf \
		$(IMAGES32_DEST)/* \
		$(ANIMATIONS_LS)


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


