
GAME_IMAGE_NAMES := \
	add_column.svg \
	add_row.svg \
	allblocks.png \
	allthings.png \
	bridge_rising_left.svg \
	bridge_rising_right.svg \
	entrance.png \
	exit.png \
	land_block_1.png \
	land_rising_left_1.png \
	land_rising_right_1.png \
	metal_block_1.png \
	rabbit_stand_left.svg \
	rabbit_stand_right.svg \
	rabbot_stand_left.svg \
	rabbot_stand_right.svg \
	remove_block.png \
	remove_column.svg \
	remove_rabbit.svg \
	remove_row.svg \
	remove_thing.png \
	save.svg \
	undo.svg \
	redo.svg \


IMAGES_DIR := www/images
IMAGES := $(foreach X,${GAME_IMAGE_NAMES}, ${IMAGES_DIR}/${X})


all: compile test

www:
	mkdir -p www

www/images/%: images-src/% www/images
	cp $< $@

www/images: www
	mkdir -p $@

www/index.html: index.html www
	cp $< $@

www/style.css: style.css www
	cp $< $@

images: ${IMAGES_DIR} ${IMAGES}

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
