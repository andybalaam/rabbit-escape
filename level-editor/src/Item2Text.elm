module Item2Text exposing (CharItem(..), StarLine(..), toText, toItems)


import Dict
import EveryDict


import Rabbit exposing (Direction(..), Rabbit, makeRabbit)
import World exposing
    ( Block(..)
    , BlockMaterial(..)
    , BlockShape(..)
    )


type StarLine = StarLine String


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


b2t : EveryDict.EveryDict Block Char
b2t =
    EveryDict.fromList (List.map swap t2bList)


type CharItem =
      StarChar
    | BlockChar Block
    | RabbitChar Rabbit


t2rList : List (Char, Rabbit)
t2rList =
    [ ('j', makeRabbit 0 0 Left)
    , ('r', makeRabbit 0 0 Right)
    ]


t2r : Dict.Dict Char Rabbit
t2r =
    Dict.fromList t2rList


r2t : EveryDict.EveryDict Rabbit Char
r2t =
    EveryDict.fromList (List.map swap t2rList)


addRabbitCoords : Int -> Int -> Rabbit -> Rabbit
addRabbitCoords y x rabbit =
    { rabbit | x = x, y = y }


toItems : Int -> Int -> Char -> Result String CharItem
toItems y x c =
    if c == '*' then
        Ok StarChar
    else case Dict.get c t2b of
        Just block ->
            Ok (BlockChar block)
        Nothing ->
            case Dict.get c t2r of
                Just rabbit ->
                    Ok (RabbitChar (addRabbitCoords y x rabbit))
                Nothing      ->
                    Err
                        (  "Unrecognised character '"
                        ++ (String.fromChar c)
                        ++ "'."
                        )


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


blockToChars : Block -> List Char
blockToChars block =
    case EveryDict.get block b2t of
        Just ' ' -> []
        Just c -> [c]
        Nothing -> Debug.crash ("Unknown block!" ++ (toString block))


toText : Block -> List Rabbit -> (Char, Maybe StarLine)
toText block rabbits =
    let
        chars = blockToChars block ++ rabbitsToChars rabbits
    in
        case chars of
            [] -> (' ', Nothing)
            [c] -> (c, Nothing)
            default -> ('*', Just (StarLine (String.fromList chars)))
