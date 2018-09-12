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


import Rabbit exposing
    ( Direction(..)
    , Rabbit
    , RabbitType(..)
    , makeRabbit
    , makeRabbot
    )

import Thing exposing (Thing(..), TokenType(..))

import World exposing
    ( Block(..)
    , BlockMaterial(..)
    , BlockShape(..)
    )


type StarContent =
    StarContent String


blockToChar : Block -> Char
blockToChar block =
    case block of
        NoBlock -> ' '
        Block Earth Flat -> '#'
        Block Earth UpLeft -> '\\'
        Block Earth UpRight -> '/'
        Block Earth BridgeUpLeft -> ')'
        Block Earth BridgeUpRight -> '('
        Block Metal _ -> 'M'


charToBlock : Char -> Maybe Block
charToBlock char =
    case char of
        ' ' -> Just NoBlock
        '#' -> Just (Block Earth Flat)
        '\\' -> Just (Block Earth UpLeft)
        '/' -> Just (Block Earth UpRight)
        ')' -> Just (Block Earth BridgeUpLeft)
        '(' -> Just (Block Earth BridgeUpRight)
        'M' -> Just (Block Metal Flat)
        _ -> Nothing


rabbitToChar : Rabbit -> Char
rabbitToChar rabbit =
    case (rabbit.typ, rabbit.dir) of
        (Normal, Left) -> 'j'
        (Normal, Right) -> 'r'
        (Rabbot, Left) -> 'y'
        (Rabbot, Right) -> 't'


charToRabbit : Char -> Maybe Rabbit
charToRabbit char =
    case char of
        'j' -> Just (makeRabbit 0 0 Left)
        'r' -> Just (makeRabbit 0 0 Right)
        'y' -> Just (makeRabbot 0 0 Left)
        't' -> Just (makeRabbot 0 0 Right)
        _ -> Nothing


thingToChar : Thing -> Char
thingToChar thing =
    case thing of
        Entrance _ _ -> 'Q'
        Exit _ _ -> 'O'
        Fire _ _ -> 'A'
        Token Bash _ _ -> 'b'
        Token Dig _ _ -> 'd'
        Token Bridge _ _ -> 'i'
        Token BlockT _ _ -> 'k'
        Token Climb _ _ -> 'c'
        Token Explode _ _ -> 'p'
        Token Brolly _ _ -> 'l'


charToThing : Char -> Maybe Thing
charToThing char =
    case char of
        'Q' -> Just (Entrance 0 0)
        'O' -> Just (Exit 0 0)
        'A' -> Just (Fire 0 0)
        'b' -> Just (Token Bash 0 0)
        'd' -> Just (Token Dig 0 0)
        'i' -> Just (Token Bridge 0 0)
        'k' -> Just (Token BlockT 0 0)
        'c' -> Just (Token Climb 0 0)
        'p' -> Just (Token Explode 0 0)
        'l' -> Just (Token Brolly 0 0)
        _ -> Nothing


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


zeroCoords : Rabbit -> Rabbit
zeroCoords rabbit =
    { rabbit | x = 0, y = 0 }


rabbitsToChars : List Rabbit -> List Char
rabbitsToChars rabbits =
    List.map rabbitToChar rabbits


thingsToChars : List Thing -> List Char
thingsToChars things =
    List.map thingToChar things


blockToChars : Block -> List Char
blockToChars block =
    case blockToChar block of
        ' ' -> []
        c -> [c]


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
