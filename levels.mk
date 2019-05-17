ANIMATIONS_DIR := src/render/src/rabbitescape/render/animations
RABBIT_ANIMATIONS := $(wildcard $(ANIMATIONS_DIR)/rabbit_*.rea)
RABBOT_ANIMATIONS := $(subst animations/rabbit,animations/rabbot,$(RABBIT_ANIMATIONS))

LEVELS_DIRS := $(shell \
	find src/engine -type d \
		-regex '.*/\(src\|test\)/rabbitescape/levels/[^/]*' \
	)

%/ls.txt: %/*.rea
	@ls $(@D) --hide=ls.txt > $(@D)/ls.txt

%/levels.txt: %/*.rel
	@./build-scripts/levelnames $(@D) > $(@D)/levels.txt

%/levels.txt: %/*/*.rel
	@./build-scripts/levelnames $(@D) > $(@D)/levels.txt


$(ANIMATIONS_DIR)/rabbot%.rea: $(ANIMATIONS_DIR)/rabbit%.rea
	./build-scripts/rea-rabbit-to-rabbot < $< > $@

levels.mk-animations: $(ANIMATIONS_DIR)/ls.txt $(RABBOT_ANIMATIONS)
	@echo ". Generating animations list"

levels.mk-levels: $(patsubst %, %/levels.txt, $(LEVELS_DIRS))
	@echo ". Generating level lists"

levels.mk-clean-animations:
	@echo ". Cleaning animation lists"
	@find ./ -name "ls.txt" -delete

levels.mk-clean-levels:
	@echo ". Cleaning levels lists"
	@find ./ -name "levels.txt" -delete
