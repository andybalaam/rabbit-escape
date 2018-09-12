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

# TODO https://elm-lang.org/0.19.0/optimize

compile: index images
	elm make \
		src/Main.elm \
		--output=www/level-editor.js

upload: compile
	rsync -r --delete \
		./www/ \
		dreamhost:artificialworlds.net/rabbit-escape/level-editor/

run:
	elm reactor

test:
	elm-test

setup:
	echo "Install Elm from https://guide.elm-lang.org/install.html"
	echo "Then 'sudo npm install -g elm-test@0.19.0-beta8' or similar"

clean:
	rm -rf www
