package rabbitescape.engine;

import java.util.ArrayList;
import java.util.List;

import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.World.CantAddTokenOutsideWorld;
import rabbitescape.engine.World.NoBlockFound;
import rabbitescape.engine.World.NoSuchAbilityInThisWorld;
import rabbitescape.engine.World.NoneOfThisAbilityLeft;
import rabbitescape.engine.World.UnableToAddToken;
import rabbitescape.engine.util.Position;

public class WorldChanges
{

    private final World world;
    public final WorldStatsListener statsListener;

    private final List<Rabbit> rabbitsToEnter = new ArrayList<Rabbit>();
    private final List<Rabbit> rabbitsToKill  = new ArrayList<Rabbit>();
    private final List<Rabbit> rabbitsToSave  = new ArrayList<Rabbit>();
    private final List<Token>  tokensToAdd    = new ArrayList<Token>();
    public  final List<Token>  tokensToRemove = new ArrayList<Token>();
    public  final List<Fire>   fireToRemove   = new ArrayList<Fire>();
    private final List<Block>  blocksToAdd    = new ArrayList<Block>();
    private final List<Block>  blocksToRemove = new ArrayList<Block>();
    public final List<Position>   blocksJustRemoved = new ArrayList<Position>();
    private final List<Position>  waterPointsToRecalculate = new ArrayList<>();

    private boolean explodeAll = false;

    private List<Rabbit> rabbitsJustEntered = new ArrayList<Rabbit>();

    public WorldChanges( World world, WorldStatsListener statsListener )
    {
        this.world = world;
        this.statsListener = statsListener;
        updateStats();
    }

    public synchronized void apply()
    {
        // Add any new things
        for ( Rabbit rabbit : rabbitsToEnter )
        {
            rabbit.calcNewState( world );
        }
        world.rabbits.addAll( rabbitsToEnter );
        world.things.addAll( tokensToAdd );
        world.blockTable.addAll( blocksToAdd );

        // Remove dead/saved rabbits, used tokens, dug out blocks
        world.rabbits.removeAll( rabbitsToKill );
        world.rabbits.removeAll( rabbitsToSave );
        world.things.removeAll(  tokensToRemove );
        world.things.removeAll( fireToRemove );
        world.blockTable.removeAll(  blocksToRemove );

        for ( Position point : waterPointsToRecalculate )
        {
            world.recalculateWaterRegions( point );
        }

        if ( rabbitsToSave.size() > 0 )
        {
            updateStats();
        }

        rabbitsToEnter.clear();
        rabbitsToKill.clear();
        rabbitsToSave.clear();
        tokensToAdd.clear();
        tokensToRemove.clear();
        fireToRemove.clear();
        blocksToAdd.clear();
        blocksToRemove.clear();
        waterPointsToRecalculate.clear();

        if ( explodeAll )
        {
            doExplodeAll();
        }
    }

    private void updateStats()
    {
        statsListener.worldStats( world.num_saved, world.num_to_save );
    }

    private void doExplodeAll()
    {
        world.num_waiting = 0;
        for ( Rabbit rabbit : world.rabbits )
        {
            rabbit.state = State.RABBIT_EXPLODING;
        }
    }

    public synchronized void revert()
    {
        revertEnterRabbits();
        revertKillRabbits();
        revertSaveRabbits();
        revertAddTokens();
        tokensToRemove.clear();
        blocksToAdd.clear();
        blocksToRemove.clear();
        waterPointsToRecalculate.clear();
    }

    private synchronized void revertEnterRabbits()
    {
        world.num_waiting += rabbitsToEnter.size();
        rabbitsToEnter.clear();
    }

    public synchronized void enterRabbit( Rabbit rabbit )
    {
        --world.num_waiting;
        rabbitsToEnter.add( rabbit );
    }

    private synchronized void revertKillRabbits()
    {
        for ( Rabbit rabbit : rabbitsToKill )
        {
            if ( rabbit.type == Rabbit.Type.RABBIT )
            {
                --world.num_killed;
            }
        }
        rabbitsToKill.clear();
    }

    public synchronized void killRabbit( Rabbit rabbit )
    {
        if ( rabbit.type == Rabbit.Type.RABBIT )
        {
            ++world.num_killed;
        }
        rabbitsToKill.add( rabbit );
    }

    private void revertSaveRabbits()
    {
        world.num_saved -= rabbitsToSave.size();
        rabbitsToSave.clear();
    }

    public synchronized void saveRabbit( Rabbit rabbit )
    {
        ++world.num_saved;
        rabbitsToSave.add( rabbit );
    }

    private synchronized void revertAddTokens()
    {
        for ( Token t : tokensToAdd )
        {
            world.abilities.put( t.type, world.abilities.get( t.type ) + 1 );
        }
        tokensToAdd.clear();
    }

    public synchronized void addToken( int x, int y, Token.Type type )
    throws UnableToAddToken // gyh 주석 : 여기서 월드 바깥을 벗어났나 체크 여기서 블록 안에 있는지 체크하면 될듯?
    {
        Integer numLeft = world.abilities.get( type );

        if ( numLeft == null )
        {
            throw new NoSuchAbilityInThisWorld( type );
        }

        if ( numLeft == 0 )
        {
            throw new NoneOfThisAbilityLeft( type );
        }

        if ( x < 0 || y < 0 || x >= world.size.width || y >= world.size.height )
        {
            throw new CantAddTokenOutsideWorld( type, x, y, world.size );
        }

        Block block = world.getBlockAt( x, y );
        if ( BehaviourTools.s_isFlat( block ) )
        {
            if(type.toString().equals("breakblock")) { //gyh 주석 : 일반블록안의 breakblock 처리, Token의 특성을 생각했을때, Rabbit이 먹고 쓰냐, Block에 바로 쓰냐 구분자를 추가하거나 Behaviour의 상속관계를 추가하면
                                                        //더 나은 설계일것같은데 일단 블록에 바로 적용되는게 breakblock밖에 없어서 이렇게 함



                tokensToAdd.add( new Token( x, y, type, world ) );
               // world.changes.removeBlockAt(x, y);
                world.abilities.put( type, numLeft - 1 ); //gyh 주석 : 해당 스테이지에서 사용가능한 토큰의 수 표시

            }

            return; //gyh 주석 : 이걸 주석처리하면 테스트에서 에러남
        }


        tokensToAdd.add( new Token( x, y, type, world ) ); //gyh주석 : 맵상에 실제로 토큰이 추가되도록 함
        world.abilities.put( type, numLeft - 1 );
    }

    public synchronized void removeToken( Token thing )
    {
        tokensToRemove.add( thing );
    }

    public synchronized void removeFire( Fire thing )
    {
        fireToRemove.add( thing );
    }

    public synchronized void addBlock( Block block )
    {
        blocksToAdd.add( block );
        waterPointsToRecalculate.add( new Position( block.x, block.y ) );
    }

    public synchronized void removeBlockAt( int x, int y )
    {
        Block block = world.getBlockAt( x, y );
        if ( block == null )
        {
            throw new NoBlockFound( x, y );
        }
        blocksJustRemoved.add( new Position( x, y ) );
        blocksToRemove.add( block );
        waterPointsToRecalculate.add( new Position( x, y ) );
    }

    public synchronized List<Thing> tokensAboutToAppear()
    {
        return new ArrayList<Thing>( tokensToAdd );
    }

    public synchronized void explodeAllRabbits()
    {
        explodeAll = true;
    }

    public List<Rabbit> rabbitsJustEntered()
    {
        return rabbitsJustEntered;
    }

    public void rememberWhatWillHappen()
    {
        rabbitsJustEntered = new ArrayList<Rabbit>( rabbitsToEnter );
    }
}
