package rabbitescape.engine.menu;

//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.*;
import static rabbitescape.engine.util.Util.*;

import java.util.List;

import rabbitescape.engine.World;

public class LevelsMenu extends Menu
{
    private final String levelsDir;
    private final LevelsCompleted levelsCompleted;

    public LevelsMenu(
        String levelsDir,
        LevelsList levelsList,
        LevelsCompleted levelsCompleted
    )
    {
        this(
            levelsDir,
            levelsCompleted,
            menuItems( levelsDir, levelsList )
        );
    }

    private LevelsMenu(
        String levelsDir, LevelsCompleted levelsCompleted, MenuItem[] items )
    {
        super( "Choose a level:", items );

        this.levelsDir = levelsDir;
        this.levelsCompleted = levelsCompleted;

        refresh();
    }

    private static MenuItem[] menuItems(
        String levelsDir,
        LevelsList levelsInfo
    )
    {
        List<LevelsList.LevelInfo> levels = levelsInfo.inDir( levelsDir );

        MenuItem[] ret = new MenuItem[ levels.size() ];

        for ( IdxObj<LevelsList.LevelInfo> info : enumerate1( levels ) )
        {
            ret[info.index - 1] = new LevelMenuItem(
                levelsDir + "/" + info.object.fileName + ".rel",
                levelsDir,
                info.index,
                true,
                info.object.name
            );
        }

        return ret;
    }

    @Override
   public void refresh()
    {
    	int x=World.star;
   	
    /*	try {
    	File f = new File("/home/furkansenyurt/Masaüstü/rabbit/rabbit-escape/rabbit-escape-engine/src/rabbitescape/levels/01_easy");
    	FileWriter fwriter = new FileWriter(f);
    	PrintWriter pw = new PrintWriter(fwriter);
    	
    		for(int i=0;i<=x;i++) {
    			pw.print("*");
    		}
    		pw.close();
    
    		
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    */
    	
    	if(x == 1) {
	        int lastEnabled =
	            levelsCompleted.highestLevelCompleted( levelsDir ) + 1;
	
	        for ( IdxObj<MenuItem> item : enumerate1( items ) )
	        {
	            item.object.enabled = ( item.index <= lastEnabled );
	        }
	    
    	}
    	else if(x == 2) {
    		int lastEnabled =
    	            levelsCompleted.highestLevelCompleted( levelsDir ) + 2;
    	
    	        for ( IdxObj<MenuItem> item : enumerate1( items ) )
    	        {
    	            item.object.enabled = ( item.index <= lastEnabled );
    	        }
    		
    	}
    	else if(x == 3) {
    		int lastEnabled =
    	            levelsCompleted.highestLevelCompleted( levelsDir ) + 3;
    	
    	        for ( IdxObj<MenuItem> item : enumerate1( items ) )
    	        {
    	            item.object.enabled = ( item.index <= lastEnabled );
    	        }
    	}
    	World.star=0;
    	
    
    	
    	
    }
    
    
 
    
    
   
    
    
    
}
