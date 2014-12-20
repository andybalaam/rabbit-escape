
CLASSPATH=rabbit-escape-engine/bin/:rabbit-escape-render/bin/:rabbit-escape-ui-text/bin/:rabbit-escape-ui-swing/bin/

IMAGES32_DEST=rabbit-escape-ui-swing/src/rabbitescape/ui/swing/images32
ANDROIDIMAGESMDPI_DEST=rabbit-escape-ui-android/app/src/main/res/drawable-mdpi
ANDROIDIMAGESHDPI_DEST=rabbit-escape-ui-android/app/src/main/res/drawable-hdpi
ANDROIDIMAGESXHDPI_DEST=rabbit-escape-ui-android/app/src/main/res/drawable-xhdpi

IMAGESSRC := $(wildcard images-src/*.svg)
IMAGES32 := $(IMAGESSRC:images-src/%.svg=$(IMAGES32_DEST)/%.png)

MUSICSRC := $(wildcard music-src/*.flac)

ANDROIDIMAGESMDPI := $(IMAGESSRC:images-src/%.svg=$(ANDROIDIMAGESMDPI_DEST)/%.png)
ANDROIDIMAGESHDPI := $(IMAGESSRC:images-src/%.svg=$(ANDROIDIMAGESHDPI_DEST)/%.png)
ANDROIDIMAGESXHDPI := $(IMAGESSRC:images-src/%.svg=$(ANDROIDIMAGESXHDPI_DEST)/%.png)

ANIMATIONS_DIR := rabbit-escape-render/src/rabbitescape/render/animations
LEVELS_DIRS := $(wildcard rabbit-escape-engine/src/rabbitescape/levels/*) \
               $(wildcard rabbit-escape-engine/test/rabbitescape/levels/*)

$(IMAGES32_DEST)/%.png: images-src/%.svg
	mkdir -p $(IMAGES32_DEST); inkscape $< --export-png=$@ --export-dpi=90

$(ANDROIDIMAGESMDPI_DEST)/%.png: images-src/%.svg
	mkdir -p $(ANDROIDIMAGESMDPI_DEST); inkscape $< --export-png=$@ --export-dpi=90

$(ANDROIDIMAGESHDPI_DEST)/%.png: images-src/%.svg
	mkdir -p $(ANDROIDIMAGESHDPI_DEST); inkscape $< --export-png=$@ --export-dpi=135

$(ANDROIDIMAGESXHDPI_DEST)/%.png: images-src/%.svg
	mkdir -p $(ANDROIDIMAGESXHDPI_DEST); inkscape $< --export-png=$@ --export-dpi=180

#$(MUSICOGG_DEST)/%.ogg: music-src/%.flac
#	mkdir -p $(MUSICOGG_DEST); avconv -i $< -c:a libvorbis -q:a 1 $@

VERSION=0.0.5

all: compile

dist: dist/rabbit-escape-generic.jar dist/rabbit-escape-${VERSION}.jar

dist/rabbit-escape-generic.jar: compile
	mkdir -p dist
	rm -f dist/rabbit-escape-generic.jar
	cd rabbit-escape-engine/bin; \
		jar -cf ../../dist/rabbit-escape-generic.jar `find ./`
	cd rabbit-escape-render/bin; \
		jar -uf ../../dist/rabbit-escape-generic.jar `find ./`

dist/rabbit-escape-${VERSION}.jar: compile
	mkdir -p dist
	rm -f dist/rabbit-escape-${VERSION}.jar
	cd rabbit-escape-engine/bin; \
		jar -cf ../../dist/rabbit-escape-${VERSION}.jar `find ./`
	cd rabbit-escape-render/bin; \
		jar -uf ../../dist/rabbit-escape-${VERSION}.jar `find ./`
	cd rabbit-escape-ui-text/bin; \
		jar -uf ../../dist/rabbit-escape-${VERSION}.jar `find ./`
	cd rabbit-escape-ui-swing/bin; \
		jar -uf ../../dist/rabbit-escape-${VERSION}.jar `find ./`
	jar -ufm dist/rabbit-escape-${VERSION}.jar MANIFEST.MF

images: $(IMAGES32)

#music: $(MUSICOGG)

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
	rm -rf dist

clean-images:
	rm -rf $(IMAGES32_DEST)/* $(ANDROIDIMAGESMDPI_DEST)/* $(ANDROIDIMAGESHDPI_DEST)/* $(ANDROIDIMAGESXHDPI_DEST)/*

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

# Android
# -------

rabbit-escape-ui-android/app/libs/rabbit-escape-generic.jar: dist/rabbit-escape-generic.jar
	mkdir -p rabbit-escape-ui-android/app/libs/
	cp dist/rabbit-escape-generic.jar rabbit-escape-ui-android/app/libs/

android-images-mdpi: $(ANDROIDIMAGESMDPI)
android-images-hdpi: $(ANDROIDIMAGESHDPI)
android-images-xhdpi: $(ANDROIDIMAGESXHDPI)

android-images: android-images-mdpi android-images-hdpi android-images-xhdpi

android-pre: android-images rabbit-escape-ui-android/app/libs/rabbit-escape-generic.jar

