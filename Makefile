
IMAGESSRC := $(wildcard images-src/*.svg)
IMAGES32 := $(IMAGESSRC:images-src/%.svg=images32/%.png)

ANIMATIONS32 := animations32/walk.gif animations32/bash.gif

images32/%.png: images-src/%.svg
	mkdir -p images32; inkscape $< --export-png=$@

animations32/%.gif: $(IMAGES32)
	mkdir -p animations32; avconv -y -f image2 -r 10 -i images32/rabbit-$*-%02d.png -pix_fmt rgb24 -an $@

all: compile

images: $(IMAGES32)

animations: $(ANIMATIONS32)

compile:
	ant compile

clean:
	rm -rf rabbit-escape-engine/bin/* rabbit-escape-ui-text/bin/* images32/* animations32/*

run: compile
	java -cp rabbit-escape-engine/bin/:rabbit-escape-ui-text/bin/ rabbitescape.ui.text.Main levels/basic/level_01.rel

test: compile
	# Work around what looks like an Ant 1.9 bug by including the classpath here
	CLASSPATH=lib/org.hamcrest.core_1.3.0.jar:lib/junit.jar ant test


