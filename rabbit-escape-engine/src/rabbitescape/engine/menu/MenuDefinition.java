package rabbitescape.engine.menu;

import static rabbitescape.engine.menu.MenuConstruction.*;
import static rabbitescape.engine.util.Util.*;

import rabbitescape.engine.menu.MenuItem.Type;
import rabbitescape.engine.util.Util.IdxObj;

public class MenuDefinition
{
    public static final LevelSetDef[] allLevelSets = new LevelSetDef[]
    {
        new LevelSetDef( "Easy",     "01_easy"     ),
        new LevelSetDef( "Medium",   "02_medium"   ),
        new LevelSetDef( "Hard",     "03_hard"     ),
        new LevelSetDef( "Outdoors", "04_outdoors" ),
        new LevelSetDef( "Arcade",   "05_arcade"   )
    };

    public static Menu mainMenu(
        LevelsCompleted levelsCompleted, boolean includeLoadLevel )
    {
        return menu(
            "Welcome to Rabbit Escape!",
            item(
                "Start Game",
                menu(
                    "Choose a set of levels:",
                    items( levelsCompleted, allLevelSets )
                ),
                true
            ),
            item( "About",      Type.ABOUT, true ),
            maybeItem(
                includeLoadLevel,
                "Custom Levels",
                menu(
                    "Get from file or network",
                    item( "Load Level", Type.LOAD, true ),
                    item( "GitHub Issue", Type.GITHUB_ISSUE, true )
                ),
                true
            ),
            item( "Quit",       Type.QUIT,  true )
        );
    }

    private static MenuItem[] items(
        LevelsCompleted levelsCompleted,
        LevelSetDef[] levelSets
    )
    {
        LevelsList levelsList = LoadLevelsList.load( levels( levelSets ) );

        MenuItem[] ret = new MenuItem[ levelSets.length ];
        for ( IdxObj<LevelSetDef> setI : enumerate( levelSets ) )
        {
            LevelSetDef set = setI.object;
            ret[setI.index] = item(
                set.name,
                new LevelsMenu( set.dirName, levelsList, levelsCompleted ),
                true
            );
        }
        return ret;
    }

    private static class LevelSetDef
    {
        public final String name;
        public final String dirName;

        public LevelSetDef( String name, String dirName )
        {
            this.name = name;
            this.dirName = dirName;
        }
    }

    private static LevelsList levels( LevelSetDef[] levelSets )
    {
        return new LevelsList(
            list(
                map(
                    new Function<LevelSetDef, LevelsList.LevelSetInfo>()
                    {
                        @Override
                        public LevelsList.LevelSetInfo apply(
                            LevelSetDef levelSetDef )
                        {
                            return new LevelsList.LevelSetInfo(
                                levelSetDef.name, levelSetDef.dirName, null );
                        }
                    },
                    levelSets
                )
            ).toArray( new LevelsList.LevelSetInfo[ levelSets.length ] )
        );
    }
}
