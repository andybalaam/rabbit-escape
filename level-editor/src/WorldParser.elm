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


parse : String -> String -> Result String World
parse comment text_world =
    Result.map
        (makeWorld comment)
        (Result.map makeBlockGrid (toLines text_world))


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
    combine (List.map toBlock (String.toList line))


toBlock : Char -> Result String Block
toBlock c =
    case c of
        ' ' -> Ok NoBlock
        '#' -> Ok (Block Earth Flat)
        _ -> Err ("Unrecognised character '" ++ (String.fromChar c) ++ "'.")


{- From https://github.com/circuithub/elm-result-extra -}
{-| Combine a list of results into a single result (holding a list).
-}
combine : List (Result x a) -> Result x (List a)
combine = List.foldr (Result.map2 (::)) (Ok [])
