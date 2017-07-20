module Item2Text exposing (Items, toText, toItems)


import Dict
import EveryDict


import Rabbit exposing (Direction(..), Rabbit, makeRabbit)
import World exposing
    ( Block(..)
    , BlockMaterial(..)
    , BlockShape(..)
    )


blockItem : Block -> Items
blockItem block =
    { block = block
    , rabbits = []
    }


rabbitItem : Rabbit -> Items
rabbitItem rabbit =
    { block = NoBlock
    , rabbits = [rabbit]
    }


t2bList : List (Char, Items)
t2bList =
    [ (' ',  blockItem NoBlock)
    , ('#',  blockItem (Block Earth Flat))
    , ('\\', blockItem (Block Earth UpLeft))
    , ('/',  blockItem (Block Earth UpRight))
    , (')',  blockItem (Block Earth BridgeUpLeft))
    , ('(',  blockItem (Block Earth BridgeUpRight))
    , ('M',  blockItem (Block Metal Flat))
    , ('j',  rabbitItem (makeRabbit 0 0 Left))
    , ('r',  rabbitItem (makeRabbit 0 0 Right))
    ]


t2b : Dict.Dict Char Items
t2b =
    Dict.fromList t2bList


swap : (a, b) -> (b, a)
swap (x, y) =
    (y, x)


b2t : EveryDict.EveryDict Items Char
b2t =
    EveryDict.fromList (List.map swap t2bList)


type alias Items =
    { block : Block
    , rabbits : List Rabbit
    }


addRabbitCoords : Int -> Int -> Rabbit -> Rabbit
addRabbitCoords y x rabbit =
    { rabbit | x = x, y = y }


addCoords : Int -> Int -> Items -> Items
addCoords y x items =
    { items | rabbits = List.map (addRabbitCoords y x) items.rabbits }


toItems : Int -> Int -> Char -> Result String Items
toItems y x c =
    case Dict.get c t2b of
        Just items ->
            Ok (addCoords y x items)
        Nothing ->
            Err ("Unrecognised character '" ++ (String.fromChar c) ++ "'.")


toText : Block -> Char
toText b =
    case EveryDict.get {block = b, rabbits = []} b2t of
        Just c ->
            c
        Nothing ->
            Debug.crash "Unknown block!"
