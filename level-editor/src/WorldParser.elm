module WorldParser exposing (parse)


import World exposing (
    Block(..),
    BlockMaterial(..),
    BlockShape(..),
    Grid,
    World,
    makeBlockGrid,
    makeWorld
    )


import Item2Text exposing (Items, toItems)


parse : String -> String -> Result String World
parse comment textWorld =
    let
        items = parseLines textWorld
        grid = Result.map makeBlockGrid (Result.map findBlocks items)
        rabbits = Ok []
    in
        Result.map2 (makeWorld comment) grid rabbits


removeFirstIfEmpty : List String -> List String
removeFirstIfEmpty lines =
    case lines of
        "" :: t -> t
        x -> x


removeLastIfEmpty : List String -> List String
removeLastIfEmpty lines =
    List.reverse (removeFirstIfEmpty (List.reverse lines))


split : String -> List String
split s =
    removeLastIfEmpty (String.lines s)


findBlocks : List (List Items) -> List (List Block)
findBlocks items =
    List.map (\item -> List.map .block item) items


parseLines : String -> Result String (List (List Items))
parseLines manyLines =
    combine (List.map parseLine (split manyLines))


parseLine : String -> Result String (List Items)
parseLine line =
    combine (List.map toItems (String.toList line))


{- From https://github.com/circuithub/elm-result-extra -}
{-| Combine a list of results into a single result (holding a list).
-}
combine : List (Result x a) -> Result x (List a)
combine = List.foldr (Result.map2 (::)) (Ok [])
