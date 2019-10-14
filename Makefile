
MAKEFLAGS += --warn-undefined-variables

CLASSPATH=src/engine/bin/:src/render/bin/:src/ui-text/bin/:src/ui-swing/bin/

VERSION=0.13.1

all: test android-compile

snapshot: android-pre dist
ifndef SNAPSHOT_VERSION
	$(error Run like this: SNAPSHOT_VERSION=0.8.0.3 make snapshot)
endif
	git tag v${SNAPSHOT_VERSION}
	- rm dist/rabbit-escape-generic.jar
	rename 's/${VERSION}/${SNAPSHOT_VERSION}/' dist/rabbit-escape-*
	scp dist/rabbit-escape-* dreamhost:artificialworlds.net/rabbit-escape/snapshots/

dist: checks dist-swing dist-android-release-signed

dist-swing: dist/rabbit-escape-${VERSION}.jar

dist/rabbit-escape-generic.jar: compile-noui-notests
	@echo ". Building generic jar $@"
	@mkdir -p dist
	@rm -f dist/rabbit-escape-generic.jar
	@cd src/engine/bin; \
		jar -cf ../../../dist/rabbit-escape-generic.jar `find ./`
	@cd src/render/bin; \
		jar -uf ../../../dist/rabbit-escape-generic.jar `find ./`
	@chmod ug+rw $@
	@chmod o+r $@

dist/rabbit-escape-${VERSION}.jar: compile
	@echo ". Building Swing jar $@"
	@mkdir -p dist
	@rm -f dist/rabbit-escape-${VERSION}.jar
	@cd src/engine/bin; \
		jar -cf ../../../dist/rabbit-escape-${VERSION}.jar `find ./`
	@cd src/render/bin; \
		jar -uf ../../../dist/rabbit-escape-${VERSION}.jar `find ./`
	@cd src/ui-text/bin; \
		jar -uf ../../../dist/rabbit-escape-${VERSION}.jar `find ./`
	@cd src/ui-swing/bin; \
		jar -uf ../../../dist/rabbit-escape-${VERSION}.jar `find ./`
	@jar -ufm dist/rabbit-escape-${VERSION}.jar MANIFEST.MF
	@chmod ug+rwx $@
	@chmod o+r $@

include checks.mk
include compile.mk
include levels.mk
include images.mk
include sounds.mk


checks: checks.mk-checks
compile: compile.mk-compile
compile-noui-notests: compile.mk-compile-noui-notests
images: images.mk
sounds: sounds.mk-sounds
music: sounds.mk-music
animations: levels.mk-animations
levels: levels.mk-levels


clean: levels.mk-clean-levels levels.mk-clean-animations
	@echo ". Cleaning compiled Java"
	@mkdir -p src/engine/bin && touch src/engine/bin/touchfile && rm -r src/engine/bin/*
	@mkdir -p src/render/bin && touch src/render/bin/touchfile && rm -r src/render/bin/*
	@mkdir -p src/ui-text/bin/touchfile && touch src/ui-text/bin/touchfile && rm -r src/ui-text/bin/*
	@mkdir -p src/ui-swing/bin/touchfile && touch src/ui-swing/bin/touchfile && rm -r src/ui-swing/bin/*
	@find ./ -empty -type d -delete
	@echo ". Cleaning dist/"
	@mkdir -p dist && rm -r dist

clean-all: clean images.mk-clean sounds.mk-clean-sounds sounds.mk-clean-music clean-doxygen clean-android

remove-trailing:
	git status --porcelain | sed 's_^...__' | grep '\.java$$' - | xargs perl -p -i -e 's/[ \t]+$$//'

test: compile
	@echo ". Running unit tests"
	@./build-scripts/test-java "${TEST_CLASSPATH}" src/engine/bin

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

android/app/libs/rabbit-escape-generic.jar: dist/rabbit-escape-generic.jar
	@echo ". Copying $@"
	@mkdir -p android/app/libs/
	@cp dist/rabbit-escape-generic.jar android/app/libs/

android-pre: \
	images \
	sounds.mk-android-sounds \
	sounds.mk-android-music \
	android/app/libs/rabbit-escape-generic.jar

# Special target for F-Droid that does not build the sound or music files,
# to avoid needing sox to be installed on the F-Droid server.
android-pre-fdroid: \
	images \
	android/app/libs/rabbit-escape-generic.jar

android-compile: android-pre
	@echo ". Compiling Android code"
	@cd android && \
	${GRADLE} compilePaidDebugSources

android-debug: \
	android/app/build/outputs/apk/paid/debug/app-paid-debug.apk \
	android/app/build/outputs/apk/free/debug/app-free-debug.apk

android-debug-test: android-debug \
	android/app/build/outputs/apk/paid/debug/app-paid-debug-androidTest.apk \
	android/app/build/outputs/apk/free/debug/app-free-debug-androidTest.apk

android/app/build/outputs/apk/paid/debug/app-paid-debug.apk: android-pre
	@echo ". Building $@"
	@cd android && ${GRADLE} assemblePaidDebug

android/app/build/outputs/apk/free/debug/app-free-debug.apk: android-pre
	@echo ". Building $@"
	@cd android && ${GRADLE} assembleFreeDebug

android/app/build/outputs/apk/free/debug/app-free-debug-androidTest.apk: android-pre
	@echo ". Building $@"
	@cd android && ${GRADLE} assembleFreeDebugAndroidTest

android/app/build/outputs/apk/paid/debug/app-paid-debug-androidTest.apk: android-pre
	@echo ". Building $@"
	@cd android && ${GRADLE} assemblePaidDebugAndroidTest

KEY_STORE_PASSWORD_FILE := $(HOME)/pw/android-key-store-password.txt
KEY_PASSWORD_FILE := $(HOME)/pw/android-key-password.txt

# ls commands are to check that the password files exist
dist-android-release-signed: android-pre
	@echo ". Building and signing release apk dist/rabbit-escape-${VERSION}.apk and dist/rabbit-escape-free-${VERSION}.apk"
	@ls $(KEY_STORE_PASSWORD_FILE) > /dev/null && \
	ls $(KEY_PASSWORD_FILE) > /dev/null && \
	cd android && \
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
	cd android && ${GRADLE} clean
	rm -f android/app/libs/rabbit-escape-generic.jar

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
