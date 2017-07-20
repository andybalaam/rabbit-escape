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

        grid =
            items                                       -- List (List Items)
            |> Result.map (List.map (List.map .block))  -- List (List Block)
            |> Result.map makeBlockGrid                 -- Grid

        rabbits =
            items                              -- List (List Items)
            |> Result.map List.concat          -- List Items
            |> Result.map (List.map .rabbits)  -- List (List Rabbit)
            |> Result.map List.concat          -- List Rabbit
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


parseLines : String -> Result String (List (List Items))
parseLines manyLines =
    combine (List.indexedMap parseLine (split manyLines))


parseLine : Int -> String -> Result String (List Items)
parseLine y line =
    combine (List.indexedMap (toItems y) (String.toList line))


{- From https://github.com/circuithub/elm-result-extra -}
{-| Combine a list of results into a single result (holding a list).
-}
combine : List (Result x a) -> Result x (List a)
combine = List.foldr (Result.map2 (::)) (Ok [])
