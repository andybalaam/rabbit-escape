
DOC=../../doc/minilevel
GIF=$(DOC)/rabbitescape-minilevel.gif
FRAMES=../../bin/minilevel/frames

$(GIF): rabbitescape-minilevel-recording.mkv
	rm -rf $(FRAMES)
	mkdir -p $(FRAMES)
	avconv -i $< -r 10 -vf crop=342:310:48:30 -ss 00:00:02.500 $(FRAMES)/frame-%04d.png
	mkdir -p $(DOC)
	convert -layers Optimize $(FRAMES)/*.png $@

clean:
	rm -rf $(GIF) $(FRAMES)

