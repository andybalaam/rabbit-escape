package rabbitescape;

public interface Behaviour
{
    boolean describeChanges(
        Rabbit rabbit, World world, ChangeDescription ret );
    boolean behave( Rabbit rabbit, World world );
}
