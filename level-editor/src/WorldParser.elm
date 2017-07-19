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


import Item2Text exposing (toItems)


parse : String -> String -> Result String World
parse comment text_world =
    Result.map2
        (makeWorld comment)
        (Result.map makeBlockGrid (toLines text_world))
        (Ok [])


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


toLines : String -> Result String (List (List Block))
toLines manyLines =
    combine (List.map toLine (split manyLines))


toLine : String -> Result String (List Block)
toLine line =
    combine (List.map toItems (String.toList line))


{- From https://github.com/circuithub/elm-result-extra -}
{-| Combine a list of results into a single result (holding a list).
-}
combine : List (Result x a) -> Result x (List a)
combine = List.foldr (Result.map2 (::)) (Ok [])
