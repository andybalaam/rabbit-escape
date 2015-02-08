
DOC=../../doc/www
GIF1=$(DOC)/demo.gif
FRAMES=../../bin/demo/frames

all: $(GIF1)

$(GIF1): demo.mkv
	rm -rf $(FRAMES)
	mkdir -p $(FRAMES)
	#avconv -i $< -r 10 -vf crop=790:223:50:54 -ss 00:00:08.200 -t 00:00:34 $(FRAMES)/frame-%04d.png
	avconv -i $< -r 10 -ss 00:00:02.000 -t 00:00:36 $(FRAMES)/frame-%04d.png
	mkdir -p $(DOC)
	convert -layers Optimize $(FRAMES)/*.png $@

clean:
	rm -rf $(GIF1) $(FRAMES)

