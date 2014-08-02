
CLASSPATH=rabbit-escape-engine/bin/:rabbit-escape-render/bin/:rabbit-escape-ui-text/bin/:rabbit-escape-ui-swing/bin/

IMAGES32_DEST=rabbit-escape-ui-swing/src/rabbitescape/ui/swing/images32

IMAGESSRC := $(wildcard images-src/*.svg)
IMAGES32 := $(IMAGESSRC:images-src/%.svg=$(IMAGES32_DEST)/%.png)

$(IMAGES32_DEST)/%.png: images-src/%.svg
	mkdir -p $(IMAGES32_DEST); inkscape $< --export-png=$@

all: compile

images: $(IMAGES32)

rabbit-escape-render/src/rabbitescape/render/animations/ls.txt: rabbit-escape-render/src/rabbitescape/render/animations/*.rea
	cd rabbit-escape-render/src/rabbitescape/render/animations; ls *.rea > ls.txt

animations: rabbit-escape-render/src/rabbitescape/render/animations/ls.txt

compile: images animations
	ant compile

clean:
	rm -rf \
		rabbit-escape-engine/bin/* \
		rabbit-escape-render/bin/* \
		rabbit-escape-ui-text/bin/* \
		rabbit-escape-ui-swing/bin/* \
		images32/* \
		rabbit-escape-render/src/rabbitescape/render/animations/ls.txt


run: compile
	java -cp $(CLASSPATH) rabbitescape.ui.text.TextMain levels/basic/level_01.rel

rungui: compile
	java -cp $(CLASSPATH) rabbitescape.ui.swing.SwingMain levels/basic/level_03.rel

runat: compile
	java -cp $(CLASSPATH) rabbitescape.ui.swing.AnimationTester

test: compile
	# Work around what looks like an Ant 1.9 bug by including the classpath here
	CLASSPATH=lib/org.hamcrest.core_1.3.0.jar:lib/junit.jar ant test


