package rabbitescape.engine.config.upgrades;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.IConfigStorage;
import rabbitescape.engine.config.IConfigUpgrade;

public class UpgradeTo01NonAndroidDummy implements IConfigUpgrade
{
    @Override
    public void run( IConfigStorage storage )
    {
        storage.set( Config.CFG_VERSION, "1" );
    }
}
