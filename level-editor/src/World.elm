module World exposing
    ( Block(..)
    , Grid
    , World
    , initWorld
    , makeBlockGrid
    , makeWorld
    )


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
    }


initBlocks : List (List Block)
initBlocks =
    [
        [ Block Earth Flat
        , Block Earth Flat
        ]
    ]


initWorld : World
initWorld =
    { comment = "Empty level"
    , blocks =
        Grid
            8 6 initBlocks
    }


makeWorld : String -> Grid Block -> World
makeWorld comment blocks =
    { comment = comment
    , blocks = blocks
    }


makeBlockGrid : List (List Block) -> Grid Block
makeBlockGrid blocks =
    case blocks of
        [] -> Grid 0 0 blocks
        x :: _ -> Grid (List.length x) (List.length blocks) blocks
