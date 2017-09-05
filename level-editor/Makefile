
GAME_IMAGE_NAMES := \
	land_block_1.png land_block_2.png land_block_3.png land_block_4.png \
	land_rising_left_1.png land_rising_left_2.png \
	land_rising_left_3.png land_rising_left_4.png \
	land_rising_right_1.png land_rising_right_2.png \
	land_rising_right_3.png land_rising_right_4.png \
	rabbit_stand_left.svg rabbit_stand_right.svg \
	save.svg \


GAME_IMAGES_DIR := www/game-images
GAME_IMAGES := $(foreach X,${GAME_IMAGE_NAMES}, ${GAME_IMAGES_DIR}/${X})


all: compile test

www:
	mkdir -p www

www/game-images/%: ../images-src/% www/game-images
	cp $< $@

www/game-images/%: images-src/% www/game-images
	cp $< $@

www/game-images: www
	mkdir -p $@

www/index.html: index.html www
	cp $< $@

www/style.css: style.css www
	cp $< $@

images: ${GAME_IMAGES_DIR} ${GAME_IMAGES}

index: www/index.html www/style.css

compile: index images
	elm-make --output=www/level-editor.js src/Main.elm

upload: compile
	rsync -r --delete ./www/ dreamhost:artificialworlds.net/rabbit-escape/level-editor/

run:
	elm-reactor

test:
	elm-test

setup:
	sudo npm install -g elm@0.18
	elm-package install -y

clean:
	rm -r www
