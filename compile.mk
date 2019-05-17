J = \
	$(shell find $(1)/src -type f -name "*.java") \
	$(shell find $(1)/test -type f -name "*.java") \

JAVA_ENGINE := $(call J,rabbit-escape-engine)
JAVA_RENDER := $(call J,rabbit-escape-render)
JAVA_UI_TEXT := $(call J,rabbit-escape-ui-text)
JAVA_UI_SWING := $(call J,rabbit-escape-ui-swing)

TEST_CLASSPATH := lib/org.hamcrest.core_1.3.0.jar:lib/junit.jar

compile.mk-compile: \
		checks \
		animations \
		levels \
		images \
		sounds \
		music \
		rabbit-escape-engine/bin/compile.touchfile \
		rabbit-escape-render/bin/compile.touchfile \
		rabbit-escape-ui-text/bin/compile.touchfile \
		rabbit-escape-ui-swing/bin/compile.touchfile

compile.mk-compile-noui-notests: \
		checks \
		animations \
		levels
	@echo ". Compiling without UI and tests"

	@rm -rf rabbit-escape-engine/bin/*
	@./build-scripts/compile-java \
		rabbit-escape-engine/bin/compilenotests.touchfile \
		"" \
		rabbit-escape-engine/bin \
		rabbit-escape-engine/src

	@rm -rf rabbit-escape-render/bin/*
	@./build-scripts/compile-java \
		rabbit-escape-render/bin/compilenotests.touchfile \
		"rabbit-escape-engine/bin" \
		rabbit-escape-render/bin \
		rabbit-escape-render/src

rabbit-escape-engine/bin/compile.touchfile: ${JAVA_ENGINE}
	@echo ". Compiling engine"
	@./build-scripts/compile-java \
		"$@" \
		"${TEST_CLASSPATH}" \
		rabbit-escape-engine/bin \
		rabbit-escape-engine/src \
		rabbit-escape-engine/test

rabbit-escape-render/bin/compile.touchfile: ${JAVA_ENGINE} ${JAVA_RENDER}
	@echo ". Compiling rendering"
	@./build-scripts/compile-java \
		"$@" \
		"${TEST_CLASSPATH}:rabbit-escape-engine/bin" \
		rabbit-escape-render/bin \
		rabbit-escape-render/src \
		rabbit-escape-render/test

rabbit-escape-ui-text/bin/compile.touchfile: ${JAVA_ENGINE} ${JAVA_RENDER} ${JAVA_UI_TEXT}
	@echo ". Compiling text UI"
	@./build-scripts/compile-java \
		"$@" \
		"${TEST_CLASSPATH}:rabbit-escape-engine/bin:rabbit-escape-render/bin" \
		rabbit-escape-ui-text/bin \
		rabbit-escape-ui-text/src \
		rabbit-escape-ui-text/test

rabbit-escape-ui-swing/bin/compile.touchfile: ${JAVA_ENGINE} ${JAVA_RENDER} ${JAVA_UI_SWING}
	@echo ". Compiling Swing UI"
	@./build-scripts/compile-java \
		"$@" \
		"${TEST_CLASSPATH}:rabbit-escape-engine/bin:rabbit-escape-render/bin" \
		rabbit-escape-ui-swing/bin \
		rabbit-escape-ui-swing/src \
		rabbit-escape-ui-swing/test
