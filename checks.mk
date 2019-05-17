ifndef MAKECMDGOALS
MAKECMDGOALS = all
endif

checks.mk-checks: checks.mk-no-make-warnings checks.mk-versioncheck checks.mk-no-awt-in-engine

# Fails if the Makefile contains any warnings
checks.mk-no-make-warnings:
	@echo ". Checking for warnings in Makefile"
	@! make -n $(MAKECMDGOALS) 2>&1 >/dev/null | grep warning

checks.mk-versioncheck:
	@echo ". Checking version number (${VERSION}) is consistent everywhere"
	@grep "version = \"${VERSION}\"" \
		src/engine/src/rabbitescape/engine/menu/AboutText.java \
		> /dev/null
	@grep "versionName \"${VERSION}\"" \
		android/app/build.gradle > /dev/null

# Fails if we use java.awt in the engine code - this is not available on Android
checks.mk-no-awt-in-engine:
	@echo ". Checking for use of java.awt in engine code"
	@! find src/engine/src -name "*.java" -print0 | xargs -0 grep 'java\.awt'
	@! find src/render/src -name "*.java" -print0 | xargs -0 grep 'java\.awt'
