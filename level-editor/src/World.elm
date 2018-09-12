module World exposing
    ( Block(..)
    , BlockMaterial(..)
    , BlockShape(..)
    , Grid
    , World
    , blocks
    , makeBlockGrid
    , makeWorld
    , rabbitsAt
    , thingsAt
    , width
    )


import MetaLines exposing (MetaLines)
import Rabbit exposing (Rabbit)
import Thing exposing (Thing)


type BlockMaterial =
    Earth | Metal


type BlockShape =
    Flat | UpRight | UpLeft | BridgeUpRight | BridgeUpLeft


type Block =
    NoBlock | Block BlockMaterial BlockShape


type Grid a =
    Grid Int Int (List (List a))


type alias World =
    { comment : String
    , blocks : Grid Block
    , rabbits : List Rabbit
    , things : List Thing
    , metaLines : MetaLines
    }


initBlocks : List (List Block)
initBlocks =
    [
        [ Block Earth Flat
        , Block Earth Flat
        ]
    ]


makeWorld :
    String ->
    Grid Block ->
    List Rabbit ->
    List Thing ->
    MetaLines ->
    World
makeWorld comment blocksGrid rabbits things metaLines =
    { comment = comment
    , blocks = blocksGrid
    , rabbits = rabbits
    , things = things
    , metaLines = metaLines
    }


makeBlockGrid : List (List Block) -> Grid Block
makeBlockGrid blocksList =
    case blocksList of
        [] -> Grid 0 0 blocksList
        x :: _ -> Grid (List.length x) (List.length blocksList) blocksList


-- How many blocks across this world
width : World -> Int
width world =
    case world.blocks of
        Grid x _ _ -> x


-- Extract the blocks from a world
blocks : World -> List (List Block)
blocks world =
    case world.blocks of
        Grid w h ls -> ls


-- Give me all rabbits at a co-ordinate
rabbitsAt : World -> Int -> Int -> List Rabbit
rabbitsAt world x y =
    List.filter (\r -> r.x == x && r.y == y) world.rabbits


thingsAt : World -> Int -> Int -> List Thing
thingsAt world x y =
    List.filter (\t -> Thing.pos t == (x, y)) world.things
