
# Ensure all intermediate files are kept
.SECONDARY:

MAKEFLAGS += --warn-undefined-variables

CLASSPATH=rabbit-escape-engine/bin/:rabbit-escape-render/bin/:rabbit-escape-ui-text/bin/:rabbit-escape-ui-swing/bin/

IMAGESSVGGEN_DEST=bin/imagessvggen
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

DPI_32 := 96     # Required Inkscape DPI setting to get a block=32x32
DPI_48 := 144
DPI_64 := 192
DPI_96 := 288
DPI_128 := 384

SVGIMAGESSRC := $(wildcard images-src/*.svg)
SVGICONSSRC  := $(wildcard images-src/icons/*.svg)

# Find all rabbits images, check whether the equivalent rabbot image
# exists.  If not, add it to a list of images to generate.
SVGRABBITS := $(wildcard images-src/rabbit_*.svg)
bit2bot = $(subst rabbit,rabbot,$(1))
botexists = $(wildcard $(call bit2bot,$(1)))
SVGGENERATERABBOTS := $(foreach F,$(SVGRABBITS),$(if $(call botexists,$(F)),,$(subst images-src,$(IMAGESSVGGEN_DEST),$(call bit2bot,$(F)))))

SVGIMAGES32  := $(SVGIMAGESSRC:images-src/%.svg=$(IMAGES32_DEST)/%.png) $(SVGICONSSRC:images-src/icons/%.svg=$(IMAGES32_DEST)/%.png) $(SVGGENERATERABBOTS:$(IMAGESSVGGEN_DEST)/%.svg=$(IMAGES32_DEST)/%.png)
SVGIMAGES64  := $(SVGIMAGESSRC:images-src/%.svg=$(IMAGES64_DEST)/%.png) $(SVGICONSSRC:images-src/icons/%.svg=$(IMAGES64_DEST)/%.png) $(SVGGENERATERABBOTS:$(IMAGESSVGGEN_DEST)/%.svg=$(IMAGES64_DEST)/%.png)
SVGIMAGES128 := $(SVGIMAGESSRC:images-src/%.svg=$(IMAGES128_DEST)/%.png) $(SVGICONSSRC:images-src/icons/%.svg=$(IMAGES128_DEST)/%.png) $(SVGGENERATERABBOTS:$(IMAGESSVGGEN_DEST)/%.svg=$(IMAGES128_DEST)/%.png)

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

SVGANDROIDIMAGES32  := $(SVGIMAGESSRC:images-src/%.svg=$(ANDROIDIMAGES32_DEST)/%.png) $(SVGGENERATERABBOTS:$(IMAGESSVGGEN_DEST)/%.svg=$(ANDROIDIMAGES32_DEST)/%.png)
SVGANDROIDIMAGES64  := $(SVGIMAGESSRC:images-src/%.svg=$(ANDROIDIMAGES64_DEST)/%.png) $(SVGGENERATERABBOTS:$(IMAGESSVGGEN_DEST)/%.svg=$(ANDROIDIMAGES64_DEST)/%.png)
SVGANDROIDIMAGES128 := $(SVGIMAGESSRC:images-src/%.svg=$(ANDROIDIMAGES128_DEST)/%.png) $(SVGGENERATERABBOTS:$(IMAGESSVGGEN_DEST)/%.svg=$(ANDROIDIMAGES128_DEST)/%.png)
PNGANDROIDIMAGES32  := $(PNGIMAGESSRC:images-src/%.png=$(ANDROIDIMAGES32_DEST)/%.png)
PNGANDROIDIMAGES64  := $(PNGIMAGESSRC:images-src/%.png=$(ANDROIDIMAGES64_DEST)/%.png)
PNGANDROIDIMAGES128 := $(PNGIMAGESSRC:images-src/%.png=$(ANDROIDIMAGES128_DEST)/%.png)

SVGANDROIDICONSMDPI  := $(SVGICONSSRC:images-src/icons/%.svg=$(ANDROIDICONSMDPI_DEST)/%.png)
SVGANDROIDICONSHDPI  := $(SVGICONSSRC:images-src/icons/%.svg=$(ANDROIDICONSHDPI_DEST)/%.png)
SVGANDROIDICONSXHDPI := $(SVGICONSSRC:images-src/icons/%.svg=$(ANDROIDICONSXHDPI_DEST)/%.png)

ANIMATIONS_DIR := rabbit-escape-render/src/rabbitescape/render/animations
RABBIT_ANIMATIONS := $(wildcard $(ANIMATIONS_DIR)/rabbit_*.rea)
RABBOT_ANIMATIONS := $(subst animations/rabbit,animations/rabbot,$(RABBIT_ANIMATIONS))

LEVELS_DIRS := $(shell \
	find rabbit-escape-engine -type d \
		-regex '.*/\(src\|test\)/rabbitescape/levels/[^/]*' \
	)

$(SOUNDSWAV_DEST)/%.wav: sounds-src/%.flac
	@echo ".. Converting sound: $@"
	@mkdir -p $(SOUNDSWAV_DEST); sox $< $@

$(ANDROIDSOUNDSOGG_DEST)/%.ogg: sounds-src/%.flac
	@echo ".. Converting sound: $@"
	@mkdir -p $(ANDROIDSOUNDSOGG_DEST); sox $< $@

$(MUSICWAV_DEST)/%.wav: music-src/%.flac
	@echo ".. Converting sound: $@"
	@mkdir -p $(MUSICWAV_DEST); sox $< $@ vol 0.4

$(ANDROIDMUSICOGG_DEST)/%.ogg: music-src/%.flac
	@echo ".. Converting sound: $@"
	@mkdir -p $(ANDROIDMUSICOGG_DEST); sox $< $@

$(IMAGESSVGGEN_DEST)/rabbot_%.svg: images-src/rabbit_%.svg
	@echo ".. Making rabbot image from rabbit one: $@"
	@mkdir -p $(IMAGESSVGGEN_DEST); ./build-scripts/rabbit-to-rabbot < $< > $@


$(IMAGES32_DEST)/%.png: $(IMAGESSVGGEN_DEST)/%.svg
	@echo ".. Converting from SVG: $@"
	@mkdir -p $(IMAGES32_DEST); inkscape $< --export-png=$@ --export-dpi=$(DPI_32) > /dev/null

$(IMAGES32_DEST)/%.png: images-src/%.svg
	@echo ".. Converting from SVG: $@"
	@mkdir -p $(IMAGES32_DEST); inkscape $< --export-png=$@ --export-dpi=$(DPI_32) > /dev/null

$(IMAGES64_DEST)/%.png: $(IMAGESSVGGEN_DEST)/%.svg
	@echo ".. Converting from SVG: $@"
	@mkdir -p $(IMAGES64_DEST); inkscape $< --export-png=$@ --export-dpi=$(DPI_64) > /dev/null

$(IMAGES64_DEST)/%.png: images-src/%.svg
	@echo ".. Converting from SVG: $@"
	@mkdir -p $(IMAGES64_DEST); inkscape $< --export-png=$@ --export-dpi=$(DPI_64) > /dev/null

$(IMAGES128_DEST)/%.png: $(IMAGESSVGGEN_DEST)/%.svg
	@echo ".. Converting from SVG: $@"
	@mkdir -p $(IMAGES128_DEST); inkscape $< --export-png=$@ --export-dpi=$(DPI_128) > /dev/null

$(IMAGES128_DEST)/%.png: images-src/%.svg
	@echo ".. Converting from SVG: $@"
	@mkdir -p $(IMAGES128_DEST); inkscape $< --export-png=$@ --export-dpi=$(DPI_128) > /dev/null

$(IMAGES32_DEST)/%.png: images-src/icons/%.svg
	@echo ".. Converting from SVG: $@"
	@mkdir -p $(IMAGES32_DEST); inkscape $< --export-png=$@ --export-dpi=$(DPI_32) > /dev/null

$(IMAGES64_DEST)/%.png: images-src/icons/%.svg
	@echo ".. Converting from SVG: $@"
	@mkdir -p $(IMAGES64_DEST); inkscape $< --export-png=$@ --export-dpi=$(DPI_64) > /dev/null

$(IMAGES128_DEST)/%.png: images-src/icons/%.svg
	@echo ".. Converting from SVG: $@"
	@mkdir -p $(IMAGES128_DEST); inkscape $< --export-png=$@ --export-dpi=$(DPI_128) > /dev/null


$(IMAGES32_DEST)/%.png: images-src/%.png
	@echo ".. Resizing PNG: $@"
	@mkdir -p $(IMAGES32_DEST); convert $< -resize 12.5% $@

$(IMAGES64_DEST)/%.png: images-src/%.png
	@echo ".. Resizing PNG: $@"
	@mkdir -p $(IMAGES64_DEST); convert $< -resize 25% $@

$(IMAGES128_DEST)/%.png: images-src/%.png
	@echo ".. Resizing PNG: $@"
	@mkdir -p $(IMAGES128_DEST); convert $< -resize 50% $@


# Android images are just copies of the desktop ones

$(ANDROIDIMAGES32_DEST)/%.png: $(IMAGES32_DEST)/%.png
	@echo ".. Copying PNG: $@"
	@mkdir -p $(ANDROIDIMAGES32_DEST); cp $< $@

$(ANDROIDIMAGES64_DEST)/%.png: $(IMAGES64_DEST)/%.png
	@echo ".. Copying PNG: $@"
	@mkdir -p $(ANDROIDIMAGES64_DEST); cp $< $@

$(ANDROIDIMAGES128_DEST)/%.png: $(IMAGES128_DEST)/%.png
	@echo ".. Copying PNG: $@"
	@mkdir -p $(ANDROIDIMAGES128_DEST); cp $< $@


# Android icons

$(ANDROIDICONSMDPI_DEST)/%.png: images-src/icons/%.svg
	@echo ".. Converting from SVG: $@"
	@mkdir -p $(ANDROIDICONSMDPI_DEST); inkscape $< --export-png=$@ --export-dpi=$(DPI_32) > /dev/null

$(ANDROIDICONSHDPI_DEST)/%.png: images-src/icons/%.svg
	@echo ".. Converting from SVG: $@"
	@mkdir -p $(ANDROIDICONSHDPI_DEST); inkscape $< --export-png=$@ --export-dpi=$(DPI_48) > /dev/null

$(ANDROIDICONSXHDPI_DEST)/%.png: images-src/icons/%.svg
	@echo ".. Converting from SVG: $@"
	@mkdir -p $(ANDROIDICONSXHDPI_DEST); inkscape $< --export-png=$@ --export-dpi=$(DPI_64) > /dev/null

$(ANDROIDICONSXXHDPI_DEST)/%.png: images-src/icons/%.svg
	@echo ".. Converting from SVG: $@"
	@mkdir -p $(ANDROIDICONSXXHDPI_DEST); inkscape $< --export-png=$@ --export-dpi=$(DPI_96) > /dev/null

$(ANDROIDICONSXXXHDPI_DEST)/%.png: images-src/icons/%.svg
	@echo ".. Converting from SVG: $@"
	@mkdir -p $(ANDROIDICONSXXXHDPI_DEST); inkscape $< --export-png=$@ --export-dpi=$(DPI_128) > /dev/null


# Will be needed again if we have some icons drawn as .png
#$(ANDROIDICONSMDPI_DEST)/%.png: images-src/icons/%.png
#	mkdir -p $(ANDROIDICONSMDPI_DEST); convert $< -resize 12.5% $@
# ... etc for other sizes

#$(MUSICOGG_DEST)/%.ogg: music-src/%.flac
#	mkdir -p $(MUSICOGG_DEST); avconv -i $< -c:a libvorbis -q:a 1 $@

VERSION=0.13

ifndef MAKECMDGOALS
MAKECMDGOALS = all
endif

all: test android-compile

# Fails if the Makefile contains any warnings
no-make-warnings:
	@echo ". Checking for warnings in Makefile"
	@! make -n $(MAKECMDGOALS) 2>&1 >/dev/null | grep warning

snapshot: snapshot-version-set android-pre dist
	git tag v${SNAPSHOT_VERSION}
	- rm dist/rabbit-escape-generic.jar
	rename 's/${VERSION}/${SNAPSHOT_VERSION}/' dist/rabbit-escape-*
	scp dist/rabbit-escape-* dreamhost:artificialworlds.net/rabbit-escape/snapshots/

snapshot-version-set:
ifndef SNAPSHOT_VERSION
	$(error Run like this: SNAPSHOT_VERSION=0.8.0.3 make snapshot)
endif

dist: no-make-warnings dist-swing dist-android-release-signed

dist-swing: dist/rabbit-escape-${VERSION}.jar

dist/rabbit-escape-generic.jar: compile-noui-notests
	@echo ". Building generic jar $@"
	@mkdir -p dist
	@rm -f dist/rabbit-escape-generic.jar
	@cd rabbit-escape-engine/bin; \
		jar -cf ../../dist/rabbit-escape-generic.jar `find ./`
	@cd rabbit-escape-render/bin; \
		jar -uf ../../dist/rabbit-escape-generic.jar `find ./`
	@chmod ug+rw $@
	@chmod o+r $@

dist/rabbit-escape-${VERSION}.jar: compile
	@echo ". Building Swing jar $@"
	@mkdir -p dist
	@rm -f dist/rabbit-escape-${VERSION}.jar
	@cd rabbit-escape-engine/bin; \
		jar -cf ../../dist/rabbit-escape-${VERSION}.jar `find ./`
	@cd rabbit-escape-render/bin; \
		jar -uf ../../dist/rabbit-escape-${VERSION}.jar `find ./`
	@cd rabbit-escape-ui-text/bin; \
		jar -uf ../../dist/rabbit-escape-${VERSION}.jar `find ./`
	@cd rabbit-escape-ui-swing/bin; \
		jar -uf ../../dist/rabbit-escape-${VERSION}.jar `find ./`
	@jar -ufm dist/rabbit-escape-${VERSION}.jar MANIFEST.MF
	@chmod ug+rwx $@
	@chmod o+r $@

images: no-make-warnings $(SVGIMAGES32) $(PNGIMAGES32) $(SVGIMAGES64) $(PNGIMAGES64) $(SVGIMAGES128) $(PNGIMAGES128)

sounds: no-make-warnings $(SOUNDSWAV)

music: no-make-warnings $(MUSICWAV)

%/ls.txt: %/*.rea
	@ls $(@D) --hide=ls.txt > $(@D)/ls.txt

%/levels.txt: %/*.rel
	@./build-scripts/levelnames $(@D) > $(@D)/levels.txt

%/levels.txt: %/*/*.rel
	@./build-scripts/levelnames $(@D) > $(@D)/levels.txt


$(ANIMATIONS_DIR)/rabbot%.rea: $(ANIMATIONS_DIR)/rabbit%.rea
	./build-scripts/rea-rabbit-to-rabbot < $< > $@

animations: no-make-warnings $(ANIMATIONS_DIR)/ls.txt $(RABBOT_ANIMATIONS)
	@echo ". Generating animations list"

levels: no-make-warnings $(patsubst %, %/levels.txt, $(LEVELS_DIRS))
	@echo ". Generating level lists"

versioncheck:
	@echo ". Checking version number (${VERSION}) is consistent everywhere"
	@grep "version = \"${VERSION}\"" \
		rabbit-escape-engine/src/rabbitescape/engine/menu/AboutText.java \
		> /dev/null
	@grep "versionName \"${VERSION}\"" \
		rabbit-escape-ui-android/app/build.gradle > /dev/null

# Fails if we use java.awt in the engine code - this is not available on Android
no-awt-in-engine:
	@echo ". Checking for use of java.awt in engine code"
	@! find rabbit-escape-engine/src -name "*.java" -print0 | xargs -0 grep 'java\.awt'
	@! find rabbit-escape-render/src -name "*.java" -print0 | xargs -0 grep 'java\.awt'

compile-noui-notests: \
		no-make-warnings \
		versioncheck \
		no-awt-in-engine \
		animations \
		levels
	@echo ". Compiling"
	@ant -quiet compile-noui-notests

compile: \
		no-make-warnings \
		versioncheck \
		no-awt-in-engine \
		animations \
		levels \
		images \
		sounds \
		music
	@echo ". Compiling"
	@ant -quiet compile

clean: no-make-warnings
	@echo ". Cleaning compiled Java, lists and dist dir"
	@mkdir -p rabbit-escape-engine/bin && touch rabbit-escape-engine/bin/touchfile && rm -r rabbit-escape-engine/bin/*
	@mkdir -p rabbit-escape-render/bin && touch rabbit-escape-render/bin/touchfile && rm -r rabbit-escape-render/bin/*
	@mkdir -p rabbit-escape-ui-text/bin/touchfile && touch rabbit-escape-ui-text/bin/touchfile && rm -r rabbit-escape-ui-text/bin/*
	@mkdir -p rabbit-escape-ui-swing/bin/touchfile && touch rabbit-escape-ui-swing/bin/touchfile && rm -r rabbit-escape-ui-swing/bin/*
	@find ./ -name "ls.txt" -delete
	@find ./ -name "levels.txt" -delete
	@find ./ -empty -type d -delete
	@mkdir -p dist && rm -r dist

clean-images: no-make-warnings
	- rm -f \
		$(IMAGESSVGGEN_DEST)/* \
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

clean-all: clean clean-images clean-sounds clean-music clean-doxygen clean-android


# Shortcut that generates all images much quicker than if Make asks Inkscape
# to generate each one individually.
all-images: $(SVGGENERATERABBOTS)
	@echo ".. Converting all 32x32 images SVG->PNG"
	mkdir -p $(IMAGES32_DEST)
	./build-scripts/bulk-convert-images images-src $(IMAGES32_DEST) $(DPI_32) > /dev/null
	./build-scripts/bulk-convert-images $(IMAGESSVGGEN_DEST) $(IMAGES32_DEST) $(DPI_32) > /dev/null
	./build-scripts/bulk-convert-images images-src/icons $(IMAGES32_DEST) $(DPI_32) > /dev/null
	mkdir -p $(IMAGES64_DEST)
	@echo ".. Converting all 64x64 images SVG->PNG"
	./build-scripts/bulk-convert-images images-src $(IMAGES64_DEST) $(DPI_64) > /dev/null
	./build-scripts/bulk-convert-images $(IMAGESSVGGEN_DEST) $(IMAGES64_DEST) $(DPI_64) > /dev/null
	./build-scripts/bulk-convert-images images-src/icons $(IMAGES64_DEST) $(DPI_64) > /dev/null
	@echo ".. Converting all 128x128 images SVG->PNG"
	mkdir -p $(IMAGES128_DEST)
	./build-scripts/bulk-convert-images images-src $(IMAGES128_DEST) $(DPI_128) > /dev/null
	./build-scripts/bulk-convert-images $(IMAGESSVGGEN_DEST) $(IMAGES128_DEST) $(DPI_128) > /dev/null
	./build-scripts/bulk-convert-images images-src/icons $(IMAGES128_DEST) $(DPI_128) > /dev/null


remove-trailing:
	git status --porcelain | sed 's_^...__' | grep '\.java$$' - | xargs perl -p -i -e 's/[ \t]+$$//'

run: compile-noui
	java -cp $(CLASSPATH) rabbitescape.ui.text.TextMain --noinput test/level_01.rel

runinteractive: compile-noui
	java -cp $(CLASSPATH) rabbitescape.ui.text.TextMain test/level_01.rel

runmenu: compile-noui
	java -cp $(CLASSPATH) rabbitescape.ui.text.TextMain

rungui: compile
	java -cp $(CLASSPATH) rabbitescape.ui.swing.SwingMain

runlevel: compile
	java -cp $(CLASSPATH) rabbitescape.ui.swing.SwingMain test/level_01.rel

runat: compile
	java -cp $(CLASSPATH) rabbitescape.ui.swing.AnimationTester

test: compile
	@echo ". Running unit tests"
	@# Work around what looks like an Ant 1.9 bug by including the classpath here
	@CLASSPATH=lib/org.hamcrest.core_1.3.0.jar:lib/junit.jar ant -quiet test

test-verbose: compile
	@echo ". Running unit tests"
	@# Work around what looks like an Ant 1.9 bug by including the classpath here
	@CLASSPATH=lib/org.hamcrest.core_1.3.0.jar:lib/junit.jar ant test

slowtest: test android-debug-test slowtest-run

# Run slow tests without building first
slowtest-run: expecttest android-smoke-tests

expecttest:
	@echo ". Running expect tests"
	./expecttests/expecttests

placeholder-all-levels:
	build-scripts/placeholder-all-levels

# Android
# -------

GRADLE := ./gradlew --daemon -q

rabbit-escape-ui-android/app/libs/rabbit-escape-generic.jar: dist/rabbit-escape-generic.jar
	@echo ". Copying generic jar into Android workspace $@"
	@mkdir -p rabbit-escape-ui-android/app/libs/
	@cp dist/rabbit-escape-generic.jar rabbit-escape-ui-android/app/libs/

android-images-32:  $(SVGANDROIDIMAGES32)  $(PNGANDROIDIMAGES32)
android-images-64:  $(SVGANDROIDIMAGES64)  $(PNGANDROIDIMAGES64)
android-images-128: $(SVGANDROIDIMAGES128) $(PNGANDROIDIMAGES128)

android-icons-mdpi:    $(SVGANDROIDICONSMDPI)
android-icons-hdpi:    $(SVGANDROIDICONSHDPI)
android-icons-xhdpi:   $(SVGANDROIDICONSXHDPI)
android-icons-xxhdpi:  $(ANDROIDICONSXXHDPI_DEST)/ic_launcher.png $(ANDROIDICONSXXHDPI_DEST)/ic_launcher_free.png
android-icons-xxxhdpi: $(ANDROIDICONSXXXHDPI_DEST)/ic_launcher.png $(ANDROIDICONSXXXHDPI_DEST)/ic_launcher_free.png

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

# Special target for F-Droid that does not build the sound or music files,
# to avoid needing sox to be installed on the F-Droid server.
android-pre-fdroid: \
	android-images \
	rabbit-escape-ui-android/app/libs/rabbit-escape-generic.jar

android-compile: android-pre
	@echo ". Compiling Android code"
	@cd rabbit-escape-ui-android && \
	${GRADLE} compilePaidDebugSources

android-debug: \
	rabbit-escape-ui-android/app/build/outputs/apk/paid/debug/app-paid-debug.apk \
	rabbit-escape-ui-android/app/build/outputs/apk/free/debug/app-free-debug.apk

android-debug-test: android-debug \
	rabbit-escape-ui-android/app/build/outputs/apk/paid/debug/app-paid-debug-androidTest.apk \
	rabbit-escape-ui-android/app/build/outputs/apk/free/debug/app-free-debug-androidTest.apk

rabbit-escape-ui-android/app/build/outputs/apk/paid/debug/app-paid-debug.apk: android-pre
	@echo ". Building $@"
	@cd rabbit-escape-ui-android && ${GRADLE} assemblePaidDebug

rabbit-escape-ui-android/app/build/outputs/apk/free/debug/app-free-debug.apk: android-pre
	@echo ". Building $@"
	@cd rabbit-escape-ui-android && ${GRADLE} assembleFreeDebug

rabbit-escape-ui-android/app/build/outputs/apk/free/debug/app-free-debug-androidTest.apk: android-pre
	@echo ". Building $@"
	@cd rabbit-escape-ui-android && ${GRADLE} assembleFreeDebugAndroidTest

rabbit-escape-ui-android/app/build/outputs/apk/paid/debug/app-paid-debug-androidTest.apk: android-pre
	@echo ". Building $@"
	@cd rabbit-escape-ui-android && ${GRADLE} assemblePaidDebugAndroidTest

KEY_STORE_PASSWORD_FILE := $(HOME)/pw/android-key-store-password.txt
KEY_PASSWORD_FILE := $(HOME)/pw/android-key-password.txt

# ls commands are to check that the password files exist
dist-android-release-signed: android-pre
	@echo ". Building and signing release apk dist/rabbit-escape-${VERSION}.apk and dist/rabbit-escape-free-${VERSION}.apk"
	@ls $(KEY_STORE_PASSWORD_FILE) > /dev/null && \
	ls $(KEY_PASSWORD_FILE) > /dev/null && \
	cd rabbit-escape-ui-android && \
	KEY_STORE_PASSWORD=`cat $(KEY_STORE_PASSWORD_FILE)` \
	KEY_PASSWORD=`cat $(KEY_PASSWORD_FILE)` \
	${GRADLE} assembleRelease && \
	mv app/build/outputs/apk/paid/release/app-paid-release.apk ../dist/rabbit-escape-${VERSION}.apk && \
	mv app/build/outputs/apk/free/release/app-free-release.apk ../dist/rabbit-escape-free-${VERSION}.apk

android-smoke-tests: android-debug-test
	@echo ". Running Android smoke tests"
	./build-scripts/android-start-emulator "android-16" "system-images;android-16;default;x86" "3.2in QVGA (ADP2)"
	./build-scripts/android-test "free" "app-free-debug"
	./build-scripts/android-test "paid" "app-paid-debug"
	./build-scripts/android-stop-emulator

clean-android:
	cd rabbit-escape-ui-android && ${GRADLE} clean
	rm rabbit-escape-ui-android/app/libs/rabbit-escape-generic.jar

# Docs
# ----

# Requires sudo apt-get install doxygen graphviz
doxygen:
	@echo ". Building doxygen docs in doc/doxygen"
	@mkdir -p doc/doxygen
	@echo PROJECT_NUMBER=$$(git log --date-order --tags --simplify-by-decoration --pretty=format:"%ci_%d" -n 1) | tr " " "_" | cat Doxyfile - | doxygen - > /dev/null

doxygen-upload: doxygen
	@echo ". Uploading doxygen docs"
	@rsync --delete -r doc/doxygen/html/ dreamhost:artificialworlds.net/rabbit-escape/doxygen/

clean-doxygen:
	@echo ". Cleaning doxygen files"
	@mkdir -p doc/doxygen && rm -r doc/doxygen

