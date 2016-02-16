Time moves upwards.

Later
-----

- Graphics in level design
- Level editor
- Fail to bridge action when bridge placed somewhere you can't bridge
- Separate rabbits who find themselves on top of each other.
- More colourful?
- Fix many things not animating nicely
- graphics:float
- action:float
- Fix Android bug where winning alert disappears when you rotate the screen
- Display level previews on menu
- Display level names on menu
- Fix bug where using the Title bar on android to go backwards makes
  Quit not work properly.
- Fix bug where diggers go too fast when you rotate the screen (see
  TestTextWorldManip.Digging_state_is_preserved_second_step)
- Slow down pause as time passes
- Pause when screen power save kicks in
- Next and Retry buttons when finished level, which update the
  bolded menu item
- Remove Settings button (Android)
- Tell you what level you are playing
- Victory screen
- favicon for web site
- Save up zoom events and do them between frames
- Consider whether http://developer.android.com/training/gestures/detector.html
  will fix the bug where we jerk-scroll after a zoom.
- climbing up a 1 high wall glitches. First animation needs to be custom
- climbing from an upward slope glitches. Also probably a downward slope.
- walking from flat to downward slope starts off with rabbit about 2 pixels too high
- after climbing and hitting head sometimes the rabbit walks on air
- show level name in menu
- front menu with quick play button that jumps to what you are stuck on
- victory message (and sound?) when you complete a level set
- jump to next level or retry when finished level
- js port (and level editor)
- when continuing to bridge rabbit floats
- when a rabbit is starting to climb it can be made to bridge
- when a rabbit is starting to bridge it can be made to bash
- after climbing up to downward slope we fall onto it which looks weird
      \
    rc#
    ###
- when I explode a blocker it jerks to the left
- Use cant_place_token sound effect when you cannot place a token
- Sounds for winning an losing a level and winning a level set

Release 0.9
-----------
- ~~Metal (undiggable) blocks~~
- ~~Fire~~
- ~~tokens resting on slopes should be higher~~

Release 0.8
-----------
- ~~Speed up button~~
- ~~Animate falling onto slopes~~
- ~~show level name and number in game~~
- ~~record walkthroughs in main ui~~
- ~~when a bridge is almost built it should behave like a bridge~~

Release 0.7
-----------
- ~~20 new levels: "arcade"~~
- ~~walkthrough solutions for each level, tested with the build~~
- ~~Automated tests that levels run OK and can be solved~~

Release 0.6
-----------

- ~~when I drop a token near the end of a bridge that is being built it should not float to the ground.~~
- ~~medium 7 can be solved because of a bug where you can walk through a slope that should hit your head~~
- ~~fix text ui and allow recording walkthroughs in it~~
- ~~20 new levels: "outdoor"~~
- ~~New music for the outdoor levels~~
- ~~Ability to play custom levels~~
- ~~Author name and url in level definitions~~
- ~~Docs on making levels~~
- ~~Docs on installing~~

Release 0.5
-----------

- ~~Description for each level~~
- ~~Hints: 2-3 per level, getting increasingly hinty~~
- ~~Tokens don''t fall through half-built bridges~~
- ~~Test for the same key added twice in a level~~
- ~~swing ui should use dialogs instead of overlay~~

Release 0.4
-----------

- ~~Improve levels~~
- ~~Fix Android not playing place token noise~~
- ~~Switch to BY-NC-SA for content~~
- ~~Update all license info (including in-game) to mention 3 licenses~~
- ~~Credit tryad on web site and about page~~
- ~~Up android volumes to the max~~
- ~~Music~~
- ~~Sound effects~~
- ~~Reverse level 2 to make it fit the bashing icon~~
- ~~Fix bug on desktop where explode all dialog does not display~~

Release 0.3.1
-------------

- ~~Improve rendering performance~~
- ~~Fix bug where rabbits go crazy after suspend/resume~~

Release 0.3
-----------

- ~~Zoom in Android (pinch, auto-zoom on start/rotate)~~
- ~~Improve Swing performance by using the same game loop as Android~~
- ~~Share more code between Android and Swing~~
- ~~Avoid casts with more generics~~
- ~~Keyboard shortcuts~~
- ~~Zoom in Swing (Ctrl+mouse wheel, buttons, auto-zoom on resize)~~
- ~~Click and drag to scroll in Swing~~
- ~~Fix slow scroll wheel speed in menu~~
- ~~Issue #2, Single window~~
- ~~Fix icon redraw bug in-game on Swing~~

Release 0.2.2
-------------

- ~~Fix a crash when rotating the screen on a level with empty description~~

Release 0.2.1
-------------

- ~~Fix bug where we crash if you place a token outside playing area~~

Release 0.2 (Android)
---------------------

- ~~Android launch icon~~
- ~~Fix bug where blockers stop being blockers when you rotate screen~~
- ~~Show the level you just played on the menu~~
- ~~Info about how many to save etc.~~
- ~~Info num tokens you have~~
- ~~Graph paper background~~
- ~~Centre level in window~~
- ~~Varied block images~~
- ~~Draw rabbits in front of objects~~
- ~~Message at the start of a level~~
- ~~Message when you lose~~
- ~~Message when you win~~
- ~~Explode all button~~
- ~~Android menus launch real levels~~
- ~~Android menus update when you win~~
- ~~Android game loop~~
- ~~Android graphics~~

Release 0.1 (Playable)
----------------------

- Don''t scroll to the top of the list after you''ve won a level
- ~~Pencil-style blocks~~
- ~~Squares shown in the background~~
- ~~Centre the game canvas when smaller than the window~~
- ~~Order and name all levels~~
- ~~20 easy levels~~
- ~~20 medium levels~~
- ~~20 hard levels~~
- ~~Per-level messages~~
- ~~Make bridge token next to wall build forwards~~
- ~~Fix bug where digging through slope then falling keeps digging.~~
- ~~Check all possible states have graphics~~
- ~~Ability to kill all remaining rabbits~~
- ~~action:explode?~~
- ~~Prevent adding tokens inside solid blocks~~
- ~~Disallow dropping tokens outside game area, and them falling outside~~
- ~~Fix bug on PC missing updates~~

Release 0.0.5 (alpha)
---------------------

- ~~User-facing web site~~
- ~~Improve ability graphics~~
- ~~Fix walking animation to be touching ground~~
- ~~graphics:climb~~
- ~~action:climb~~
- ~~graphics:block~~
- ~~action:block~~
- ~~Bug: handle digging into slope~~
- ~~Bug: handle bashing into slope~~
- ~~Bug: 2 rabbits on top of each other both become bridgers.~~
- ~~Bug: encountering a bridge near a wall should make you climb onto it.~~
- ~~Bug: bridging should stop when joins land or bridge~~
- ~~Bug: bridging should stop when bumps head~~
- ~~Bug: walking past a bridger should not jump up onto bridge~~
- ~~Bug: should fall when meeting bridge sloping towards you~~
- ~~Can bridge out of a 1-wide hole.~~
- ~~Bridge token next to a wall makes you bridge the other way~~
- ~~Beginner levels for dig, bash, bridge~~
- ~~Handle digging out of screen~~
- ~~graphics:bridge~~
- ~~action:bridge~~
- ~~Menus respond to victory~~
- ~~Android version mostly working~~
- ~~graphics:dig~~
- ~~action:dig~~
- ~~graphics:bash~~
- ~~action:bash~~
- ~~graphics:walk~~
- ~~graphics:fall~~
- ~~Swing menus~~
- ~~Swing game loop~~
- ~~Text menus~~
- ~~Text game loop~~
- ~~Basic game logic~~

