
DOC=../../doc/minilevel
GIF1=$(DOC)/rabbitescape-minilevel.gif
GIF2=$(DOC)/rabbitescape-minilevel-text.gif
FRAMES=../../bin/minilevel/frames

all: $(GIF1) $(GIF2)

$(GIF1): rabbitescape-minilevel-recording.mkv
	rm -rf $(FRAMES)
	mkdir -p $(FRAMES)
	avconv -i $< -r 10 -ss 00:00:02.000 -t 00:00:17.700 $(FRAMES)/frame-%04d.png
	#avconv -i $< -r 10 -vf crop=342:310:48:30 -ss 00:00:02.000 $(FRAMES)/frame-%04d.png
	mkdir -p $(DOC)
	convert -layers Optimize $(FRAMES)/*.png $@

$(GIF2): rabbitescape-minilevel-recording-text.mkv
	rm -rf $(FRAMES)
	mkdir -p $(FRAMES)
	avconv -i $< -r 10 -vf crop=105:170:0:0 -ss 00:00:04.300 $(FRAMES)/frame-%04d.png
	mkdir -p $(DOC)
	convert -layers Optimize $(FRAMES)/*.png $@

clean:
	rm -rf $(GIF1) $(GIF2) $(FRAMES)

