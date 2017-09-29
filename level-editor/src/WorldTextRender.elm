module WorldTextRender exposing (render)


import Item2Text exposing (StarLine(..), toText)
import World exposing (Block, Grid(..), World, blocks, rabbitsAt)


renderStarLine : StarLine -> String
renderStarLine starLine =
    case starLine of
        StarLine x -> ":*=" ++ x


render : World -> String
render world =
    let
        lines = (List.indexedMap (renderLine world) (blocks world))
        allStarLines = List.concat (List.map Tuple.second lines)
    in
        String.join
            "\n"
            (  List.map Tuple.first lines
            ++ ( List.map renderStarLine allStarLines )
            )


toText2 : World -> Int -> Int -> Block -> (Char, Maybe StarLine)
toText2 world y x block =
    toText block (rabbitsAt world x y)


catMaybes : List (Maybe a) -> List a
catMaybes maybes =
    case maybes of
        Nothing :: t -> catMaybes t
        Just h :: t -> h :: catMaybes t
        [] -> []


renderLine : World -> Int -> List Block -> (String, List StarLine)
renderLine world y blocks =
    let
        items = List.indexedMap (toText2 world y) blocks
    in
        ( String.fromList (List.map Tuple.first items)
        , catMaybes (List.map Tuple.second items)
        )
