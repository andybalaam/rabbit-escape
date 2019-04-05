
MAKEFLAGS += --warn-undefined-variables

CLASSPATH=rabbit-escape-engine/bin/:rabbit-escape-render/bin/:rabbit-escape-ui-text/bin/:rabbit-escape-ui-swing/bin/

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

VERSION=0.13.1

ifndef MAKECMDGOALS
MAKECMDGOALS = all
endif

all: test android-compile

# Fails if the Makefile contains any warnings
no-make-warnings:
	@echo ". Checking for warnings in Makefile"
	@! make -n $(MAKECMDGOALS) 2>&1 >/dev/null | grep warning

snapshot: android-pre dist
ifndef SNAPSHOT_VERSION
	$(error Run like this: SNAPSHOT_VERSION=0.8.0.3 make snapshot)
endif
	git tag v${SNAPSHOT_VERSION}
	- rm dist/rabbit-escape-generic.jar
	rename 's/${VERSION}/${SNAPSHOT_VERSION}/' dist/rabbit-escape-*
	scp dist/rabbit-escape-* dreamhost:artificialworlds.net/rabbit-escape/snapshots/

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

include images.mk

images: images.mk

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

clean-sounds: no-make-warnings
	- rm $(SOUNDSWAV_DEST)/*
	- rm $(ANDROIDSOUNDSOGG_DEST)/*

clean-music: no-make-warnings
	- rm $(MUSICWAV_DEST)/*
	- rm $(ANDROIDMUSICOGG_DEST)/*

clean-all: clean images.mk-clean clean-sounds clean-music clean-doxygen clean-android

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

android-sounds: $(ANDROIDSOUNDSOGG)

android-music: $(ANDROIDMUSICOGG)

android-pre: \
	images \
	android-sounds \
	android-music \
	rabbit-escape-ui-android/app/libs/rabbit-escape-generic.jar

# Special target for F-Droid that does not build the sound or music files,
# to avoid needing sox to be installed on the F-Droid server.
android-pre-fdroid: \
	images \
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
	rm -f rabbit-escape-ui-android/app/libs/rabbit-escape-generic.jar

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
