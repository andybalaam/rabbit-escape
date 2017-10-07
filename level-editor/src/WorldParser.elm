module WorldParser exposing
    ( Items
    , ParseErr(..)
    , resultCombine
    , mergeNewCharIntoItems
    , parse
    , parseErrToString
    , toItems
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


import Item2Text exposing
    ( CharItem(..)
    , Pos
    , StarLine(..)
    , charToBlock
    , charToRabbit
    , toText
    )


type alias Items =
    { block : Block
    , rabbits : List Rabbit
    }


type ParseErr =
      TwoBlocksInOneStarPoint Pos Char Char
    | StarInsideStarPoint Pos
    | UnrecognisedChar Pos Char


posToString : Pos -> String
posToString pos =
    (  "Line "
    ++ (toString (pos.row + 1))
    ++ ", column "
    ++ (toString (pos.col + 1))
    ++ ": "
    )


parseErrToString : ParseErr -> String
parseErrToString e =
    case e of
        TwoBlocksInOneStarPoint pos c1 c2 ->
            ( posToString pos
            ++ "Two blocks in one startpoint: '"
            ++ (toString c1)
            ++ "' and '"
            ++ (toString c2)
            ++ "'."
            )
        StarInsideStarPoint pos ->
            ( posToString pos
            ++ "Star inside a star point."
            )
        UnrecognisedChar pos ch ->
            ( posToString pos
            ++ "Unrecognised character: '"
            ++ (toString ch)
            ++ "'."
            )


blockToText : Block -> Char
blockToText block =
    Tuple.first (toText block [])


addRabbitCoords : Pos -> Rabbit -> Rabbit
addRabbitCoords pos rabbit =
    { rabbit | x = pos.col, y = pos.row }


toItems : Int -> Int -> Char -> Result ParseErr CharItem
toItems y x c =
    let
        pos = { row = y, col = x }
    in
        if c == '*' then
            Ok (StarChar pos)
        else case charToBlock c of
            Just block ->
                Ok (BlockChar pos block)
            Nothing ->
                case charToRabbit c of
                    Just rabbit ->
                        Ok (RabbitChar pos (addRabbitCoords pos rabbit))
                    Nothing ->
                        Err ( UnrecognisedChar pos c )


mergeNewCharIntoOkItems : CharItem -> Items -> Result ParseErr Items
mergeNewCharIntoOkItems chItem items =
    case chItem of
        BlockChar pos charBlock ->
            if items.block == NoBlock then
                Ok { items | block = charBlock }
            else
                Err
                    ( TwoBlocksInOneStarPoint
                        pos
                        ( blockToText items.block )
                        ( blockToText charBlock )
                    )
        RabbitChar pos charRabbit ->
            -- TODO: should we set pos of rabbit?
            Ok { items | rabbits = items.rabbits ++ [charRabbit] }
        StarChar pos ->
            Err ( StarInsideStarPoint pos )


mergeNewCharIntoItems :
    CharItem ->
    Result ParseErr Items ->
    Result ParseErr Items
mergeNewCharIntoItems chItem items =
    case items of
        Ok its -> mergeNewCharIntoOkItems chItem its
        e -> e


--integrateStarLine : StarLine -> Result ParseErr Items
--integrateStarLine (StarLine chars) =
--    let
--        noItems = { block = NoBlock, rabbits = [] }
--        itemsList = resultCombine (List.map (toItems 0 0) (String.toList chars))
--    in
--        case itemsList of
--            Ok items -> List.foldl mergeNewCharIntoItems noItems items
--            Err s -> { block = NoBlock, rabbits = [] } -- TODO: handle this error


--integrateChar :
--    CharItem ->
--    List CharItem->
--    List (List CharItem) ->
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
--    List CharItem ->
--    List (List CharItem) ->
--    List StarLine ->
--    List (List Items)
--integrateLine h t starLines =
--    case h of
--        [] -> integrateLines t starLines
--        hc :: tc -> integrateChar hc tc t starLines
--
--
--integrateLines :
--    List (List CharItem) ->
--    List StarLine ->
--    List (List Items)
--integrateLines grid starLines =
--    case grid of
--        [] -> []
--        h :: t -> integrateLine h t starLines
--
--
--integrateStarLines :
--    Result String (List (List CharItem)) ->
--    Result (List StarLine) ->
--    List (List Items)
--integrateStarLines grid starLines =
--    case (grid, starLines) of
--        (Ok g, Ok s) -> integrateLines g s
--        (Err s, Ok _) -> Err s
--        (Ok _, Err s) -> Err s
--        (Err s, Err t) -> Err (s ++ "\n" ++ t)


singleCharItemsToItems : CharItem -> Items
singleCharItemsToItems ch =
    case ch of
        BlockChar pos b -> { block = b, rabbits = [] }
        RabbitChar pos r -> { block = NoBlock, rabbits = [r] }
        StarChar pos -> { block = NoBlock, rabbits = [] }
        -- TODO: I think this method will go away - if not, the StarChar
        -- line above is wrong


parse : String -> String -> Result ParseErr World
parse comment textWorld =
    let
        allLines = split textWorld
        (grLines, stLines) = separateLineTypes allLines
        -- TODO: error on unrecognised line
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


parseGridLines : List String -> Result ParseErr (List (List CharItem))
parseGridLines lines =
    resultCombine (List.indexedMap parseGridLine lines)


parseStarLine : String -> Result String StarLine
parseStarLine line =
    if (String.left 3 line) == ":*=" then
        Ok (StarLine (String.dropLeft 3 line))
    else
        Err "Star line did not start with ':*='."


parseGridLine : Int -> String -> Result ParseErr (List CharItem)
parseGridLine y line =
    resultCombine (List.indexedMap (toItems y) (String.toList line))


{- From https://github.com/circuithub/elm-result-extra -}
{-| Combine a list of results into a single result (holding a list).
-}
resultCombine : List (Result x a) -> Result x (List a)
resultCombine = List.foldr (Result.map2 (::)) (Ok [])
