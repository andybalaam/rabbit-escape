J = \
	$(shell find $(1)/src -type f -name "*.java") \
	$(shell find $(1)/test -type f -name "*.java") \

JAVA_ENGINE := $(call J,src/engine)
JAVA_RENDER := $(call J,src/render)
JAVA_UI_TEXT := $(call J,src/ui-text)
JAVA_UI_SWING := $(call J,src/ui-swing)

TEST_CLASSPATH := lib/org.hamcrest.core_1.3.0.jar:lib/junit.jar

compile.mk-compile: \
		checks \
		animations \
		levels \
		images \
		sounds \
		music \
		src/engine/bin/compile.touchfile \
		src/render/bin/compile.touchfile \
		src/ui-text/bin/compile.touchfile \
		src/ui-swing/bin/compile.touchfile

compile.mk-compile-noui-notests: \
		checks \
		animations \
		levels
	@echo ". Compiling without UI and tests"

	@rm -rf src/engine/bin/*
	@./build-scripts/compile-java \
		src/engine/bin/compilenotests.touchfile \
		"" \
		src/engine/bin \
		src/engine/src

	@rm -rf src/render/bin/*
	@./build-scripts/compile-java \
		src/render/bin/compilenotests.touchfile \
		"src/engine/bin" \
		src/render/bin \
		src/render/src

src/engine/bin/compile.touchfile: ${JAVA_ENGINE}
	@echo ". Compiling engine"
	@./build-scripts/compile-java \
		"$@" \
		"${TEST_CLASSPATH}" \
		src/engine/bin \
		src/engine/src \
		src/engine/test

src/render/bin/compile.touchfile: ${JAVA_ENGINE} ${JAVA_RENDER}
	@echo ". Compiling rendering"
	@./build-scripts/compile-java \
		"$@" \
		"${TEST_CLASSPATH}:src/engine/bin" \
		src/render/bin \
		src/render/src \
		src/render/test

src/ui-text/bin/compile.touchfile: ${JAVA_ENGINE} ${JAVA_RENDER} ${JAVA_UI_TEXT}
	@echo ". Compiling text UI"
	@./build-scripts/compile-java \
		"$@" \
		"${TEST_CLASSPATH}:src/engine/bin:src/render/bin" \
		src/ui-text/bin \
		src/ui-text/src \
		src/ui-text/test

src/ui-swing/bin/compile.touchfile: ${JAVA_ENGINE} ${JAVA_RENDER} ${JAVA_UI_SWING}
	@echo ". Compiling Swing UI"
	@./build-scripts/compile-java \
		"$@" \
		"${TEST_CLASSPATH}:src/engine/bin:src/render/bin" \
		src/ui-swing/bin \
		src/ui-swing/src \
		src/ui-swing/test
