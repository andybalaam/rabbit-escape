module Item2Text exposing
    ( CharItem(..)
    , Pos
    , StarContent(..)
    , toText
    , charToBlock
    , charToRabbit
    , charToThing
    )


import Dict
import EveryDict


import Rabbit exposing (Direction(..), Rabbit, makeRabbit, makeRabbot)
import Thing exposing (Thing(..), TokenType(..))
import World exposing
    ( Block(..)
    , BlockMaterial(..)
    , BlockShape(..)
    )


type StarContent =
    StarContent String


swap : (a, b) -> (b, a)
swap (x, y) =
    (y, x)


t2bList : List (Char, Block)
t2bList =
    [ (' ',  NoBlock)
    , ('#',  Block Earth Flat)
    , ('\\', Block Earth UpLeft)
    , ('/',  Block Earth UpRight)
    , (')',  Block Earth BridgeUpLeft)
    , ('(',  Block Earth BridgeUpRight)
    , ('M',  Block Metal Flat)
    ]


t2b : Dict.Dict Char Block
t2b =
    Dict.fromList t2bList

charToBlock : Char -> Maybe Block
charToBlock c =
    Dict.get c t2b

b2t : EveryDict.EveryDict Block Char
b2t =
    EveryDict.fromList (List.map swap t2bList)


type alias Pos =
    { row : Int
    , col : Int
    }


type CharItem =
      StarChar Pos
    | BlockChar Pos Block
    | RabbitChar Pos Rabbit
    | ThingChar Pos Thing


posOf : CharItem -> Pos
posOf ch =
    case ch of
        StarChar p -> p
        BlockChar p _ -> p
        RabbitChar p _ -> p
        ThingChar p _ -> p


t2rList : List (Char, Rabbit)
t2rList =
    [ ('j', makeRabbit 0 0 Left)
    , ('r', makeRabbit 0 0 Right)
    , ('y', makeRabbot 0 0 Left)
    , ('t', makeRabbot 0 0 Right)
    ]


t2r : Dict.Dict Char Rabbit
t2r =
    Dict.fromList t2rList


charToRabbit : Char -> Maybe Rabbit
charToRabbit c =
    Dict.get c t2r


r2t : EveryDict.EveryDict Rabbit Char
r2t =
    EveryDict.fromList (List.map swap t2rList)


zeroCoords : Rabbit -> Rabbit
zeroCoords rabbit =
    { rabbit | x = 0, y = 0 }


rabbitToChar : Rabbit -> Char
rabbitToChar rabbit =
    case EveryDict.get (zeroCoords rabbit) r2t of
        Just c -> c
        Nothing ->
            Debug.crash ("Unknown rabbit! " ++ (toString rabbit))


rabbitsToChars : List Rabbit -> List Char
rabbitsToChars rabbits =
    List.map rabbitToChar rabbits


t2thList : List (Char, Thing)
t2thList =
    [ ('Q', Entrance 0 0)
    , ('O', Exit 0 0)
    , ('A', Fire 0 0)
    , ('b', Token Bash 0 0)
    , ('d', Token Dig 0 0)
    , ('i', Token Bridge 0 0)
    , ('k', Token BlockT 0 0)
    , ('c', Token Climb 0 0)
    , ('p', Token Explode 0 0)
    , ('l', Token Brolly 0 0)
    ]


t2th : Dict.Dict Char Thing
t2th =
    Dict.fromList t2thList


charToThing : Char -> Maybe Thing
charToThing c =
    Dict.get c t2th


th2t : EveryDict.EveryDict Thing Char
th2t =
    EveryDict.fromList (List.map swap t2thList)


thingToChar : Thing -> Char
thingToChar thing =
    case EveryDict.get (Thing.moved 0 0 thing) th2t of
        Just c -> c
        Nothing ->
            Debug.crash ("Unknown thing! " ++ (toString thing))


thingsToChars : List Thing -> List Char
thingsToChars things =
    List.map thingToChar things


blockToChars : Block -> List Char
blockToChars block =
    case EveryDict.get block b2t of
        Just ' ' -> []
        Just c -> [c]
        Nothing -> Debug.crash ("Unknown block!" ++ (toString block))


toText : Block -> List Rabbit -> List Thing -> (Char, Maybe StarContent)
toText block rabbits things =
    let
        chars =
               blockToChars block
            ++ rabbitsToChars rabbits
            ++ thingsToChars things
    in
        case chars of
            [] -> (' ', Nothing)
            [c] -> (c, Nothing)
            _ -> ('*', Just (StarContent (String.fromList chars)))
