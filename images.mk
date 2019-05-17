IMAGESSVGGEN_DEST=bin/imagessvggen
IMAGES32_DEST=src/ui-swing/src/rabbitescape/ui/swing/images32
IMAGES64_DEST=src/ui-swing/src/rabbitescape/ui/swing/images64
IMAGES128_DEST=src/ui-swing/src/rabbitescape/ui/swing/images128

ANDROIDIMAGES32_DEST=android/app/src/main/assets/images32
ANDROIDIMAGES64_DEST=android/app/src/main/assets/images64
ANDROIDIMAGES128_DEST=android/app/src/main/assets/images128
ANDROIDICONSMDPI_DEST=android/app/src/main/res/drawable-mdpi
ANDROIDICONSHDPI_DEST=android/app/src/main/res/drawable-hdpi
ANDROIDICONSXHDPI_DEST=android/app/src/main/res/drawable-xhdpi
ANDROIDICONSXXHDPI_DEST=android/app/src/main/res/drawable-xxhdpi
ANDROIDICONSXXXHDPI_DEST=android/app/src/main/res/drawable-xxxhdpi

DPI_32  := 96   # Required Inkscape DPI setting to get a block=32x32
DPI_48  := 144
DPI_64  := 192
DPI_96  := 288
DPI_128 := 384

PC_32  := "12.5%" # Required percentage size for convert to get a block=32x32
PC_64  := "25%"
PC_128 := "50%"

images.mk::
	@echo ". Converting images"
	@./build-scripts/bulk-rabbits-to-rabbots images-src $(IMAGESSVGGEN_DEST)
	@./build-scripts/bulk-convert-images images-src $(IMAGES32_DEST)  $(DPI_32)
	@./build-scripts/bulk-convert-images images-src $(IMAGES64_DEST)  $(DPI_64)
	@./build-scripts/bulk-convert-images images-src $(IMAGES128_DEST) $(DPI_128)
	@./build-scripts/bulk-convert-images $(IMAGESSVGGEN_DEST) $(IMAGES32_DEST)  $(DPI_32)
	@./build-scripts/bulk-convert-images $(IMAGESSVGGEN_DEST) $(IMAGES64_DEST)  $(DPI_64)
	@./build-scripts/bulk-convert-images $(IMAGESSVGGEN_DEST) $(IMAGES128_DEST) $(DPI_128)
	@./build-scripts/bulk-convert-images images-src/icons $(IMAGES32_DEST)  $(DPI_32)
	@./build-scripts/bulk-convert-images images-src/icons $(IMAGES64_DEST)  $(DPI_64)
	@./build-scripts/bulk-convert-images images-src/icons $(IMAGES128_DEST) $(DPI_128)
	@./build-scripts/bulk-resize-pngs images-src $(IMAGES32_DEST)  $(PC_32)
	@./build-scripts/bulk-resize-pngs images-src $(IMAGES64_DEST)  $(PC_64)
	@./build-scripts/bulk-resize-pngs images-src $(IMAGES128_DEST) $(PC_128)
	@./build-scripts/bulk-convert-images images-src/icons $(ANDROIDICONSMDPI_DEST) $(DPI_32)
	@./build-scripts/bulk-convert-images images-src/icons $(ANDROIDICONSHDPI_DEST) $(DPI_48)
	@./build-scripts/bulk-convert-images images-src/icons $(ANDROIDICONSXHDPI_DEST) $(DPI_64)
	@./build-scripts/bulk-convert-images images-src/icons $(ANDROIDICONSXXHDPI_DEST) $(DPI_96)
	@./build-scripts/bulk-convert-images images-src/icons $(ANDROIDICONSXXXHDPI_DEST) $(DPI_128)
	@mkdir -p $(ANDROIDIMAGES32_DEST)
	@mkdir -p $(ANDROIDIMAGES64_DEST)
	@mkdir -p $(ANDROIDIMAGES128_DEST)
	@cp -u $(IMAGES32_DEST)/*.png  $(ANDROIDIMAGES32_DEST)
	@cp -u $(IMAGES64_DEST)/*.png  $(ANDROIDIMAGES64_DEST)
	@cp -u $(IMAGES128_DEST)/*.png $(ANDROIDIMAGES128_DEST)

images.mk-clean:
	- rm -f \
		$(IMAGESSVGGEN_DEST)/* \
		$(IMAGES32_DEST)/* \
		$(IMAGES64_DEST)/* \
		$(IMAGES128_DEST)/* \
		$(ANDROIDIMAGES32_DEST)/* \
		$(ANDROIDIMAGES64_DEST)/* \
		$(ANDROIDIMAGES128_DEST)/* \
		$(ANDROIDICONSMDPI_DEST)/* \
		$(ANDROIDICONSHDPI_DEST)/* \
		$(ANDROIDICONSXHDPI_DEST)/* \
		$(ANDROIDICONSXXHDPI_DEST)/* \
		$(ANDROIDICONSXXXHDPI_DEST)/*
