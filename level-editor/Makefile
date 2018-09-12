IMAGES_ORIG := $(wildcard images/*)
IMAGES := $(subst images,www/images,$(IMAGES_ORIG))

all: compile test

www:
	mkdir -p www

www/images/%: images/% www/images
	cp $< $@

www/images: www
	mkdir -p $@

www/index.html: index.html www
	cp $< $@

www/style.css: style.css www
	cp $< $@

images: ${IMAGES}

index: www/index.html www/style.css

compile: index images
	elm-make --output=www/level-editor.js src/Main.elm

upload: compile
	rsync -r --delete ./www/ dreamhost:artificialworlds.net/rabbit-escape/level-editor/

run:
	elm-reactor

compile-debug: clean index images
	elm-make --output=www/level-editor.js src/Main.elm --debug

upload-debug: compile-debug
	rsync -r --delete ./www/ dreamhost:artificialworlds.net/rabbit-escape/level-editor-debug/

test:
	elm-test

setup:
	sudo apt install nodejs npm
	sudo npm install -g elm@0.18 elm-test
	elm-package install -y
	cd tests; elm-package install -y

clean:
	rm -r www
