
all: compile test

compile:
	elm-make --output=level-editor.js src/Main.elm

run:
	elm-reactor

test:
	elm-test

setup:
	sudo npm install -g elm@0.18
	elm-package install -y
