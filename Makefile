
CLASSPATH=rabbit-escape-engine/bin/:rabbit-escape-render/bin/:rabbit-escape-ui-text/bin/:rabbit-escape-ui-swing/bin/

IMAGES32_DEST=rabbit-escape-ui-swing/src/rabbitescape/ui/swing/images32

IMAGESSRC := $(wildcard images-src/*.svg)
IMAGES32 := $(IMAGESSRC:images-src/%.svg=$(IMAGES32_DEST)/%.png)

ANIMATIONS32 := animations32/walk.gif animations32/bash.gif

$(IMAGES32_DEST)/%.png: images-src/%.svg
	mkdir -p $(IMAGES32_DEST); inkscape $< --export-png=$@

animations32/%.gif: $(IMAGES32)
	mkdir -p animations32; avconv -y -f image2 -r 10 -i $(IMAGES32_DEST)/rabbit-$*-%02d.png -pix_fmt rgb24 -an $@

all: compile

images: $(IMAGES32)

animations: $(ANIMATIONS32)

compile: images
	ant compile

clean:
	rm -rf \
		rabbit-escape-engine/bin/* \
		rabbit-escape-render/bin/* \
		rabbit-escape-ui-text/bin/* \
		rabbit-escape-ui-swing/bin/* \
		images32/* \
		animations32/*


run: compile
	java -cp $(CLASSPATH) rabbitescape.ui.text.TextMain levels/basic/level_01.rel

rungui: compile
	java -cp $(CLASSPATH) rabbitescape.ui.swing.SwingMain levels/basic/level_03.rel

test: compile
	# Work around what looks like an Ant 1.9 bug by including the classpath here
	CLASSPATH=lib/org.hamcrest.core_1.3.0.jar:lib/junit.jar ant test


