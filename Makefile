
MAKEFLAGS += --warn-undefined-variables

CLASSPATH=rabbit-escape-engine/bin/:rabbit-escape-render/bin/:rabbit-escape-ui-text/bin/:rabbit-escape-ui-swing/bin/

IMAGES32_DEST=rabbit-escape-ui-swing/src/rabbitescape/ui/swing/images32
IMAGES64_DEST=rabbit-escape-ui-swing/src/rabbitescape/ui/swing/images64
IMAGES128_DEST=rabbit-escape-ui-swing/src/rabbitescape/ui/swing/images128
ANDROIDIMAGES32_DEST=rabbit-escape-ui-android/app/src/main/assets/images32
ANDROIDIMAGES64_DEST=rabbit-escape-ui-android/app/src/main/assets/images64
ANDROIDIMAGES128_DEST=rabbit-escape-ui-android/app/src/main/assets/images128

ANDROIDICONSMDPI_DEST=rabbit-escape-ui-android/app/src/main/res/drawable-mdpi
ANDROIDICONSHDPI_DEST=rabbit-escape-ui-android/app/src/main/res/drawable-hdpi
ANDROIDICONSXHDPI_DEST=rabbit-escape-ui-android/app/src/main/res/drawable-xhdpi
ANDROIDICONSXXHDPI_DEST=rabbit-escape-ui-android/app/src/main/res/drawable-xxhdpi
ANDROIDICONSXXXHDPI_DEST=rabbit-escape-ui-android/app/src/main/res/drawable-xxxhdpi

SVGIMAGESSRC := $(wildcard images-src/*.svg)
SVGICONSSRC  := $(wildcard images-src/icons/*.svg)
SVGIMAGES32  := $(SVGIMAGESSRC:images-src/%.svg=$(IMAGES32_DEST)/%.png) $(SVGICONSSRC:images-src/icons/%.svg=$(IMAGES32_DEST)/%.png)
SVGIMAGES64  := $(SVGIMAGESSRC:images-src/%.svg=$(IMAGES64_DEST)/%.png) $(SVGICONSSRC:images-src/icons/%.svg=$(IMAGES64_DEST)/%.png)
SVGIMAGES128 := $(SVGIMAGESSRC:images-src/%.svg=$(IMAGES128_DEST)/%.png) $(SVGICONSSRC:images-src/icons/%.svg=$(IMAGES128_DEST)/%.png)

PNGIMAGESSRC := $(wildcard images-src/*.png)
PNGIMAGES32  := $(PNGIMAGESSRC:images-src/%.png=$(IMAGES32_DEST)/%.png)
PNGIMAGES64  := $(PNGIMAGESSRC:images-src/%.png=$(IMAGES64_DEST)/%.png)
PNGIMAGES128 := $(PNGIMAGESSRC:images-src/%.png=$(IMAGES128_DEST)/%.png)

SOUNDSWAV_DEST := rabbit-escape-ui-swing/src/rabbitescape/ui/swing/sounds
ANDROIDSOUNDSOGG_DEST := rabbit-escape-ui-android/app/src/main/assets/sounds

MUSICWAV_DEST := rabbit-escape-ui-swing/src/rabbitescape/ui/swing/music
ANDROIDMUSICOGG_DEST := rabbit-escape-ui-android/app/src/main/assets/music

MUSICSRC  := $(wildcard music-src/*.flac)
SOUNDSSRC := $(wildcard sounds-src/*.flac)

SOUNDSWAV := $(SOUNDSSRC:sounds-src/%.flac=$(SOUNDSWAV_DEST)/%.wav)
ANDROIDSOUNDSOGG := $(SOUNDSSRC:sounds-src/%.flac=$(ANDROIDSOUNDSOGG_DEST)/%.ogg)

MUSICWAV := $(MUSICSRC:music-src/%.flac=$(MUSICWAV_DEST)/%.wav)
ANDROIDMUSICOGG := $(MUSICSRC:music-src/%.flac=$(ANDROIDMUSICOGG_DEST)/%.ogg)

SVGANDROIDIMAGES32  := $(SVGIMAGESSRC:images-src/%.svg=$(ANDROIDIMAGES32_DEST)/%.png)
SVGANDROIDIMAGES64  := $(SVGIMAGESSRC:images-src/%.svg=$(ANDROIDIMAGES64_DEST)/%.png)
SVGANDROIDIMAGES128 := $(SVGIMAGESSRC:images-src/%.svg=$(ANDROIDIMAGES128_DEST)/%.png)
PNGANDROIDIMAGES32  := $(PNGIMAGESSRC:images-src/%.png=$(ANDROIDIMAGES32_DEST)/%.png)
PNGANDROIDIMAGES64  := $(PNGIMAGESSRC:images-src/%.png=$(ANDROIDIMAGES64_DEST)/%.png)
PNGANDROIDIMAGES128 := $(PNGIMAGESSRC:images-src/%.png=$(ANDROIDIMAGES128_DEST)/%.png)

SVGANDROIDICONSMDPI  := $(SVGICONSSRC:images-src/icons/%.svg=$(ANDROIDICONSMDPI_DEST)/%.png)
SVGANDROIDICONSHDPI  := $(SVGICONSSRC:images-src/icons/%.svg=$(ANDROIDICONSHDPI_DEST)/%.png)
SVGANDROIDICONSXHDPI := $(SVGICONSSRC:images-src/icons/%.svg=$(ANDROIDICONSXHDPI_DEST)/%.png)

ANIMATIONS_DIR := rabbit-escape-render/src/rabbitescape/render/animations
LEVELS_DIRS := $(wildcard rabbit-escape-engine/src/rabbitescape/levels/*) \
               $(wildcard rabbit-escape-engine/test/rabbitescape/levels/*)

$(SOUNDSWAV_DEST)/%.wav: sounds-src/%.flac
	mkdir -p $(SOUNDSWAV_DEST); sox $< $@

$(ANDROIDSOUNDSOGG_DEST)/%.ogg: sounds-src/%.flac
	mkdir -p $(ANDROIDSOUNDSOGG_DEST); sox $< $@

$(MUSICWAV_DEST)/%.wav: music-src/%.flac
	mkdir -p $(MUSICWAV_DEST); sox $< $@ vol 0.4

$(ANDROIDMUSICOGG_DEST)/%.ogg: music-src/%.flac
	mkdir -p $(ANDROIDMUSICOGG_DEST); sox $< $@

$(IMAGES32_DEST)/%.png: images-src/%.svg
	mkdir -p $(IMAGES32_DEST); inkscape $< --export-png=$@ --export-dpi=90

$(IMAGES64_DEST)/%.png: images-src/%.svg
	mkdir -p $(IMAGES64_DEST); inkscape $< --export-png=$@ --export-dpi=180

$(IMAGES128_DEST)/%.png: images-src/%.svg
	mkdir -p $(IMAGES128_DEST); inkscape $< --export-png=$@ --export-dpi=360

$(IMAGES32_DEST)/%.png: images-src/icons/%.svg
	mkdir -p $(IMAGES32_DEST); inkscape $< --export-png=$@ --export-dpi=90

$(IMAGES64_DEST)/%.png: images-src/icons/%.svg
	mkdir -p $(IMAGES64_DEST); inkscape $< --export-png=$@ --export-dpi=180

$(IMAGES128_DEST)/%.png: images-src/icons/%.svg
	mkdir -p $(IMAGES128_DEST); inkscape $< --export-png=$@ --export-dpi=360


$(IMAGES32_DEST)/%.png: images-src/%.png
	mkdir -p $(IMAGES32_DEST); convert $< -resize 12.5% $@

$(IMAGES64_DEST)/%.png: images-src/%.png
	mkdir -p $(IMAGES64_DEST); convert $< -resize 25% $@

$(IMAGES128_DEST)/%.png: images-src/%.png
	mkdir -p $(IMAGES128_DEST); convert $< -resize 50% $@


# Android images are just copies of the desktop ones

$(ANDROIDIMAGES32_DEST)/%.png: $(IMAGES32_DEST)/%.png
	mkdir -p $(ANDROIDIMAGES32_DEST); cp $< $@

$(ANDROIDIMAGES64_DEST)/%.png: $(IMAGES64_DEST)/%.png
	mkdir -p $(ANDROIDIMAGES64_DEST); cp $< $@

$(ANDROIDIMAGES128_DEST)/%.png: $(IMAGES128_DEST)/%.png
	mkdir -p $(ANDROIDIMAGES128_DEST); cp $< $@


# Android icons

$(ANDROIDICONSMDPI_DEST)/%.png: images-src/icons/%.svg
	mkdir -p $(ANDROIDICONSMDPI_DEST); inkscape $< --export-png=$@ --export-dpi=90

$(ANDROIDICONSHDPI_DEST)/%.png: images-src/icons/%.svg
	mkdir -p $(ANDROIDICONSHDPI_DEST); inkscape $< --export-png=$@ --export-dpi=135

$(ANDROIDICONSXHDPI_DEST)/%.png: images-src/icons/%.svg
	mkdir -p $(ANDROIDICONSXHDPI_DEST); inkscape $< --export-png=$@ --export-dpi=180

$(ANDROIDICONSXXHDPI_DEST)/%.png: images-src/icons/%.svg
	mkdir -p $(ANDROIDICONSXXHDPI_DEST); inkscape $< --export-png=$@ --export-dpi=270

$(ANDROIDICONSXXXHDPI_DEST)/%.png: images-src/icons/%.svg
	mkdir -p $(ANDROIDICONSXXXHDPI_DEST); inkscape $< --export-png=$@ --export-dpi=360


# Will be needed again if we have some icons drawn as .png
#$(ANDROIDICONSMDPI_DEST)/%.png: images-src/icons/%.png
#	mkdir -p $(ANDROIDICONSMDPI_DEST); convert $< -resize 12.5% $@
# ... etc for other sizes

#$(MUSICOGG_DEST)/%.ogg: music-src/%.flac
#	mkdir -p $(MUSICOGG_DEST); avconv -i $< -c:a libvorbis -q:a 1 $@

VERSION=0.6.1

ifndef MAKECMDGOALS
MAKECMDGOALS = all
endif

all: compile

# Fails if the Makefile contains any warnings
no-make-warnings:
	! make -n $(MAKECMDGOALS) 2>&1 >/dev/null | grep warning

dist: no-make-warnings dist-swing dist-android-release-signed

dist-swing: dist/rabbit-escape-${VERSION}.jar

dist/rabbit-escape-generic.jar: compile
	mkdir -p dist
	rm -f dist/rabbit-escape-generic.jar
	cd rabbit-escape-engine/bin; \
		jar -cf ../../dist/rabbit-escape-generic.jar `find ./`
	cd rabbit-escape-render/bin; \
		jar -uf ../../dist/rabbit-escape-generic.jar `find ./`
	chmod ug+rw $@
	chmod o+r $@

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
	chmod ug+rwx $@
	chmod o+r $@

images: no-make-warnings $(SVGIMAGES32) $(PNGIMAGES32) $(SVGIMAGES64) $(PNGIMAGES64) $(SVGIMAGES128) $(PNGIMAGES128)

sounds: no-make-warnings $(SOUNDSWAV)

music: no-make-warnings $(MUSICWAV)

%/ls.txt: %/*.re*
	ls $(@D) --hide=ls.txt > $(@D)/ls.txt

animations: no-make-warnings $(ANIMATIONS_DIR)/ls.txt

levels: no-make-warnings $(patsubst %, %/ls.txt, $(LEVELS_DIRS))

versioncheck:
	grep "version = \"${VERSION}\"" rabbit-escape-engine/src/rabbitescape/engine/menu/AboutText.java
	grep "versionName \"${VERSION}\"" rabbit-escape-ui-android/app/build.gradle

# Fails if we use java.awt in the engine code - this is not available on Android
no-awt-in-engine:
	! find rabbit-escape-engine/src -name "*.java" -print0 | xargs -0 grep 'java\.awt'
	! find rabbit-escape-render/src -name "*.java" -print0 | xargs -0 grep 'java\.awt'

compile-noui: \
		no-make-warnings \
		versioncheck \
		no-awt-in-engine \
		animations \
		levels
	ant compile

compile: compile-noui images sounds music

clean: no-make-warnings
	- rm -r \
		rabbit-escape-engine/bin/* \
		rabbit-escape-render/bin/* \
		rabbit-escape-ui-text/bin/* \
		rabbit-escape-ui-swing/bin/*
	find ./ -name "ls.txt" -delete
	- rm -r dist

clean-images: no-make-warnings
	- rm \
		$(IMAGES32_DEST)/* \
		$(IMAGES64_DEST)/* \
		$(IMAGES128_DEST)/* \
		$(ANDROIDIMAGES32_DEST)/* \
		$(ANDROIDIMAGES64_DEST)/* \
		$(ANDROIDIMAGES128_DEST)/* \
		$(ANDROIDICONSMDPI_DEST)/* \
		$(ANDROIDICONSHDPI_DEST)/* \
		$(ANDROIDICONSXHDPI_DEST)/* \
		$(ANDROIDICONSXXHDPI_DEST)/* \
		$(ANDROIDICONSXXXHDPI_DEST)/*

clean-sounds: no-make-warnings
	- rm $(SOUNDSWAV_DEST)/*
	- rm $(ANDROIDSOUNDSOGG_DEST)/*

clean-music: no-make-warnings
	- rm $(MUSICWAV_DEST)/*
	- rm $(ANDROIDMUSICOGG_DEST)/*

clean-all: clean clean-images clean-sounds clean-music clean-doxygen

run: compile-noui
	java -cp $(CLASSPATH) rabbitescape.ui.text.TextMain test/level_01.rel

runinteractive: compile-noui
	java -cp $(CLASSPATH) rabbitescape.ui.text.TextMain test/level_01.rel --interactive

runmenu: compile-noui
	java -cp $(CLASSPATH) rabbitescape.ui.text.TextMain

rungui: compile
	java -cp $(CLASSPATH) rabbitescape.ui.swing.SwingMain

runlevel: compile
	java -cp $(CLASSPATH) rabbitescape.ui.swing.SwingMain test/level_01.rel

runat: compile
	java -cp $(CLASSPATH) rabbitescape.ui.swing.AnimationTester

test: compile
	# Work around what looks like an Ant 1.9 bug by including the classpath here
	CLASSPATH=lib/org.hamcrest.core_1.3.0.jar:lib/junit.jar ant test

slowtest: test
	./slowtests/slowtests

# Android
# -------

rabbit-escape-ui-android/app/libs/rabbit-escape-generic.jar: dist/rabbit-escape-generic.jar
	mkdir -p rabbit-escape-ui-android/app/libs/
	cp dist/rabbit-escape-generic.jar rabbit-escape-ui-android/app/libs/

android-images-32:  $(SVGANDROIDIMAGES32)  $(PNGANDROIDIMAGES32)
android-images-64:  $(SVGANDROIDIMAGES64)  $(PNGANDROIDIMAGES64)
android-images-128: $(SVGANDROIDIMAGES128) $(PNGANDROIDIMAGES128)

android-icons-mdpi:    $(SVGANDROIDICONSMDPI)
android-icons-hdpi:    $(SVGANDROIDICONSHDPI)
android-icons-xhdpi:   $(SVGANDROIDICONSXHDPI)
android-icons-xxhdpi:  $(ANDROIDICONSXXHDPI_DEST)/ic_launcher.png
android-icons-xxxhdpi: $(ANDROIDICONSXXXHDPI_DEST)/ic_launcher.png

android-images: \
	android-images-32 \
	android-images-64 \
	android-images-128 \
	android-icons-mdpi \
	android-icons-hdpi \
	android-icons-xhdpi \
	android-icons-xxhdpi \
	android-icons-xxxhdpi

android-sounds: $(ANDROIDSOUNDSOGG)

android-music: $(ANDROIDMUSICOGG)

android-pre: \
	android-images \
	android-sounds \
	android-music \
	rabbit-escape-ui-android/app/libs/rabbit-escape-generic.jar


android-debug: app/build/outputs/apk/app-debug.apk

app/build/outputs/apk/app-debug.apk: android-pre
	cd rabbit-escape-ui-android && ./gradlew assembleDebug

dist-android-release-signed: dist/rabbit-escape-${VERSION}.apk

KEY_STORE_PASSWORD_FILE := $(HOME)/pw/android-key-store-password.txt
KEY_PASSWORD_FILE := $(HOME)/pw/android-key-password.txt

dist/rabbit-escape-${VERSION}.apk: android-pre
	# Check the password files exist before we start
	ls $(KEY_STORE_PASSWORD_FILE) && \
	ls $(KEY_PASSWORD_FILE) && \
	cd rabbit-escape-ui-android && \
	KEY_STORE_PASSWORD=`cat $(KEY_STORE_PASSWORD_FILE)` \
	KEY_PASSWORD=`cat $(KEY_PASSWORD_FILE)` \
	./gradlew assembleRelease && \
	mv app/build/outputs/apk/app-release.apk ../dist/rabbit-escape-${VERSION}.apk

# Requires sudo apt-get install doxygen graphviz
doxygen:
	mkdir -p doc/doxygen
	echo PROJECT_NUMBER=$$(git log --date-order --tags --simplify-by-decoration --pretty=format:"%ci_%d" -n 1) | tr " " "_" | cat Doxyfile - | doxygen -

doxygen-upload: doxygen
	rsync --delete -r doc/doxygen/html/ dreamhost:artificialworlds.net/rabbit-escape/doxygen/

clean-doxygen:
	- rm -r doc/doxygen

