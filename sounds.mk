SOUNDSWAV_DEST := src/ui-swing/src/rabbitescape/ui/swing/sounds
ANDROIDSOUNDSOGG_DEST := android/app/src/main/assets/sounds

MUSICWAV_DEST := src/ui-swing/src/rabbitescape/ui/swing/music
ANDROIDMUSICOGG_DEST := android/app/src/main/assets/music

MUSICSRC  := $(wildcard music-src/*.flac)
SOUNDSSRC := $(wildcard sounds-src/*.flac)

SOUNDSWAV := $(SOUNDSSRC:sounds-src/%.flac=$(SOUNDSWAV_DEST)/%.wav)
ANDROIDSOUNDSOGG := $(SOUNDSSRC:sounds-src/%.flac=$(ANDROIDSOUNDSOGG_DEST)/%.ogg)

MUSICWAV := $(MUSICSRC:music-src/%.flac=$(MUSICWAV_DEST)/%.wav)
ANDROIDMUSICOGG := $(MUSICSRC:music-src/%.flac=$(ANDROIDMUSICOGG_DEST)/%.ogg)

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

sounds.mk-sounds: $(SOUNDSWAV)

sounds.mk-music: $(MUSICWAV)

sounds.mk-clean-sounds:
	- rm $(SOUNDSWAV_DEST)/*
	- rm $(ANDROIDSOUNDSOGG_DEST)/*

sounds.mk-clean-music:
	- rm $(MUSICWAV_DEST)/*
	- rm $(ANDROIDMUSICOGG_DEST)/*

sounds.mk-android-sounds: $(ANDROIDSOUNDSOGG)

sounds.mk-android-music: $(ANDROIDMUSICOGG)
