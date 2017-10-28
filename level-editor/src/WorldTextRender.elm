module WorldTextRender exposing (render)


import Dict


import Item2Text exposing (StarContent(..), toText)
import MetaLines exposing
    ( MetaLines
    , MetaValue(..)
    , ValueInsert
    , ValueExtract
    , defaultMeta
    , valuesList
    )
import World exposing (Block, Grid(..), World, blocks, rabbitsAt, thingsAt)


renderStarLine : StarContent -> String
renderStarLine starContent =
    case starContent of
        StarContent x -> ":*=" ++ x


renderMetaLine : (String, MetaValue a) -> String
renderMetaLine (name, value) =
       ":"
    ++ name
    ++ "="
    ++ case value of
        MetaValue n -> toString n


metaValues : MetaLines -> List (String, MetaValue Int)
metaValues lines =
    let
        toValue
            : (String, ValueExtract, ValueInsert a)
            -> List (String, MetaValue Int)
        toValue (name, f, _) =
            let
                val = f lines
            in
                if val == (f defaultMeta) then
                    []
                else
                    [(name, val)]
    in
        List.concat (List.map toValue valuesList)


renderMetaLines : World -> List String
renderMetaLines world =
    List.map renderMetaLine (metaValues world.metaLines)


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
            ++ renderMetaLines world
            )


toText2 : World -> Int -> Int -> Block -> (Char, Maybe StarContent)
toText2 world y x block =
    toText block (rabbitsAt world x y) (thingsAt world x y)


catMaybes : List (Maybe a) -> List a
catMaybes maybes =
    case maybes of
        Nothing :: t -> catMaybes t
        Just h :: t -> h :: catMaybes t
        [] -> []


renderLine : World -> Int -> List Block -> (String, List StarContent)
renderLine world y blocks =
    let
        items = List.indexedMap (toText2 world y) blocks
    in
        ( String.fromList (List.map Tuple.first items)
        , catMaybes (List.map Tuple.second items)
        )
