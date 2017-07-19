module World exposing
    ( Block(..)
    , BlockMaterial(..)
    , BlockShape(..)
    , Grid
    , World
    , blocks
    , makeBlockGrid
    , makeWorld
    )


import Rabbit exposing (Rabbit)


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
    }


initBlocks : List (List Block)
initBlocks =
    [
        [ Block Earth Flat
        , Block Earth Flat
        ]
    ]


makeWorld : String -> Grid Block -> List Rabbit -> World
makeWorld comment blocks rabbits =
    { comment = comment
    , blocks = blocks
    , rabbits = rabbits
    }


makeBlockGrid : List (List Block) -> Grid Block
makeBlockGrid blocks =
    case blocks of
        [] -> Grid 0 0 blocks
        x :: _ -> Grid (List.length x) (List.length blocks) blocks


-- Extract the blocks from a world
blocks : World -> List (List Block)
blocks world =
    case world.blocks of
        Grid w h ls -> ls
