module WorldParser exposing
    ( Items
    , ParseErr(..)
    , resultCombine
    , mergeNewCharIntoItems
    , parse
    )


import Rabbit exposing (Rabbit)
import World exposing (
    Block(..),
    BlockMaterial(..),
    BlockShape(..),
    Grid,
    World,
    makeBlockGrid,
    makeWorld
    )


import Item2Text exposing (SingleCharItems(..), StarLine(..), toItems, toText)


type alias Items =
    { block : Block
    , rabbits : List Rabbit
    }


type alias Pos =
    ( Int, Int )  -- row, column


type ParseErr =
      TwoBlocksInOneStarPoint Pos Char Char
    | StarInsideStarPoint Pos


blockToText : Block -> Char
blockToText block =
    Tuple.first (toText block [])


mergeNewCharIntoOkItems :
    Pos -> SingleCharItems -> Items -> Result ParseErr Items
mergeNewCharIntoOkItems pos chItem items =
    case chItem of
        BlockChar charBlock ->
            if items.block == NoBlock then
                Ok { items | block = charBlock }
            else
                Err
                    ( TwoBlocksInOneStarPoint
                        pos
                        ( blockToText items.block )
                        ( blockToText charBlock )
                    )
        RabbitChar charRabbit ->
            Ok { items | rabbits = items.rabbits ++ [charRabbit] }
        StarChar ->
            Err ( StarInsideStarPoint pos )


mergeNewCharIntoItems :
    Pos ->
    SingleCharItems ->
    Result ParseErr Items ->
    Result ParseErr Items
mergeNewCharIntoItems pos chItem items =
    case items of
        Ok its -> mergeNewCharIntoOkItems pos chItem its
        e -> e


--integrateStarLine : StarLine -> Items
--integrateStarLine (StarLine chars) =
--    let
--        noItems = { block = NoBlock, rabbits = [] }
--        itemsList = resultCombine (List.map (toItems 0 0) (String.toList chars))
--    in
--        case itemsList of
--            Ok okItems -> List.foldl combineItems noItems okItems
--            Err s -> { block = NoBlock, rabbits = [] } -- TODO: handle this error


--integrateChar :
--    SingleCharItems ->
--    List SingleCharItems->
--    List (List SingleCharItems) ->
--    List StarLine ->
--    List (List Items)
--integrateChar hc tc t starLines =
--    case hc of
--        StarChar ->
--            case starLines of
--                [] -> Err "Not enough star lines!"
--                hs :: ts ->
--                    (integrateStarLine hs) :: integrateLine tc t ts
--        BlockChar b ->
--            { block = b, rabbits = [] }
--        RabbitChar r ->
--            { block = NoBlock, rabbits = [r] }
--
--
--integrateLine :
--    List SingleCharItems ->
--    List (List SingleCharItems) ->
--    List StarLine ->
--    List (List Items)
--integrateLine h t starLines =
--    case h of
--        [] -> integrateLines t starLines
--        hc :: tc -> integrateChar hc tc t starLines
--
--
--integrateLines :
--    List (List SingleCharItems) ->
--    List StarLine ->
--    List (List Items)
--integrateLines grid starLines =
--    case grid of
--        [] -> []
--        h :: t -> integrateLine h t starLines
--
--
--integrateStarLines :
--    Result String (List (List SingleCharItems)) ->
--    Result (List StarLine) ->
--    List (List Items)
--integrateStarLines grid starLines =
--    case (grid, starLines) of
--        (Ok g, Ok s) -> integrateLines g s
--        (Err s, Ok _) -> Err s
--        (Ok _, Err s) -> Err s
--        (Err s, Err t) -> Err (s ++ "\n" ++ t)


singleCharItemsToItems : SingleCharItems -> Items
singleCharItemsToItems ch =
    case ch of
        BlockChar b -> { block = b, rabbits = [] }
        RabbitChar r -> { block = NoBlock, rabbits = [r] }
        StarChar -> { block = NoBlock, rabbits = [] } -- TODO


parse : String -> String -> Result String World
parse comment textWorld =
    let
        allLines = split textWorld
        (grLines, stLines) = separateLineTypes allLines
        charItems = parseGridLines grLines
        starLines = parseStarLines stLines
        items = Result.map (List.map (List.map singleCharItemsToItems)) charItems
--        items = integrateStarLines grLines stLines

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


isStarLine : String -> Bool
isStarLine line =
    String.left 3 line == ":*="


notStarLine : String -> Bool
notStarLine line =
    not (isStarLine line)


separateLineTypes : List String -> (List String, List String)
separateLineTypes lines =
    let
        grLines = List.filter notStarLine lines
        stLines = List.filter isStarLine lines
    in
        (grLines, stLines)


parseStarLines : List String -> Result String (List StarLine)
parseStarLines lines =
    resultCombine (List.map parseStarLine lines)


parseGridLines : List String -> Result String (List (List SingleCharItems))
parseGridLines lines =
    resultCombine (List.indexedMap parseGridLine lines)


parseStarLine : String -> Result String StarLine
parseStarLine line =
    if (String.left 3 line) == ":*=" then
        Ok (StarLine (String.dropLeft 3 line))
    else
        Err "Star line did not start with ':*='."


parseGridLine : Int -> String -> Result String (List SingleCharItems)
parseGridLine y line =
    resultCombine (List.indexedMap (toItems y) (String.toList line))


{- From https://github.com/circuithub/elm-result-extra -}
{-| Combine a list of results into a single result (holding a list).
-}
resultCombine : List (Result x a) -> Result x (List a)
resultCombine = List.foldr (Result.map2 (::)) (Ok [])
