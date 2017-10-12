module WorldParser exposing
    ( Items
    , ParseErr(..)
    , StarLine
    , integrateSquare
    , integrateLine
    , integrateLines
    , resultCombine
    , mergeNewCharIntoItems
    , parse
    , parseErrToString
    , makeStarLine
    , starLineToItems
    , toCharItem
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
    | StarLineDidNotStartWithColonStarEquals Pos String
    | NotEnoughStarLines Pos
    | TooManyStarLines Pos


type alias Line =
    { row : Int, content : String }


type alias StarLine =
    { row : Int
    , chars : List Char
    }


makeLine : Int -> String -> Line
makeLine row content =
    { row = row, content = content }


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
        StarLineDidNotStartWithColonStarEquals pos line ->
            ( posToString pos
            ++ "Star line '"
            ++ line
            ++ "' did not start with ':+='.  This should never happen."
            )
        NotEnoughStarLines pos ->
            ( posToString pos
            ++ "More stars (*) in level description than star lines (:*=)."
            )
        TooManyStarLines pos ->
            ( posToString pos
            ++ "Too many star lines (:*=) - not enough stars (*) in grid."
            )


blockToText : Block -> Char
blockToText block =
    Tuple.first (toText block [])


addRabbitCoords : Pos -> Rabbit -> Rabbit
addRabbitCoords pos rabbit =
    { rabbit | x = pos.col, y = pos.row }


toCharItem : Maybe Pos -> Int -> Int -> Char -> Result ParseErr CharItem
toCharItem gridPos y x c =
    let
        literalPos = { row = y, col = x }
        pos = case gridPos of
            Just p -> p
            Nothing -> literalPos
    in
        if c == '*' then
            Ok (StarChar literalPos)
        else case charToBlock c of
            Just block ->
                Ok (BlockChar literalPos block)
            Nothing ->
                case charToRabbit c of
                    Just rabbit ->
                        Ok
                            (RabbitChar
                                literalPos
                                (addRabbitCoords pos rabbit)
                            )
                    Nothing ->
                        Err ( UnrecognisedChar literalPos c )


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


starLineToItems : StarLine -> Pos -> Result ParseErr Items
starLineToItems starLine gridPos =
    let
        noItems = Ok { block = NoBlock, rabbits = [] }
        -- Items start at column 4, after ":*="
        toCharItemAt col = toCharItem (Just gridPos) starLine.row (3 + col)
        itemsList = resultCombine (List.indexedMap toCharItemAt starLine.chars)
    in
        case itemsList of
            Ok items -> List.foldl mergeNewCharIntoItems noItems items
            Err e -> Err e


integrateSquare :
    CharItem ->
    List StarLine ->
    Result ParseErr (Items, List StarLine)
integrateSquare ch starLines =
    case ch of
        StarChar pos ->
            case starLines of
                starLine :: tail ->
                    case starLineToItems starLine pos of
                        Ok items -> Ok (items, tail)
                        Err e -> Err e
                [] -> Err (NotEnoughStarLines pos)
        BlockChar _ b ->
            Ok
                ( { block = b, rabbits = [] }
                , starLines
                )
        RabbitChar pos r ->
            Ok
                ( { block = NoBlock, rabbits = [(addRabbitCoords pos r)] }
                , starLines
                )


integrateLine :
    List CharItem ->
    List StarLine ->
    Result ParseErr (List Items, List StarLine)
integrateLine charItems starLines =
    case charItems of
        ch :: others ->
            case integrateSquare ch starLines of
                Err e -> Err e
                Ok (items, newStarLines) ->
                    case integrateLine others newStarLines of
                        Err e -> Err e
                        Ok (itemsList, sl) -> Ok (items :: itemsList, sl)
        default -> Ok ([], starLines)


integrateLines :
    List (List CharItem) ->
    List StarLine ->
    Result ParseErr (List (List Items), List StarLine)
integrateLines grid starLines =
    case grid of
        line :: otherLines ->
            case integrateLine line starLines of
                Err e -> Err e
                Ok (newLine, newStarLines) ->
                    case integrateLines otherLines newStarLines of
                        Err e ->
                            Err e
                        Ok (newOtherLines, sl) ->
                            Ok (newLine :: newOtherLines, sl)
        default -> Ok ([], starLines)


integrateStarLines :
    Result ParseErr (List (List CharItem)) ->
    Result ParseErr (List StarLine) ->
    Result ParseErr (List (List Items))
integrateStarLines charItems starLines =
    case charItems of
        Err e -> Err e
        Ok items ->
            case starLines of
                Err e -> Err e
                Ok stars ->
                    case integrateLines items stars of
                        Err e ->
                            Err e
                        Ok (items, []) ->  -- No extra star lines - all good
                            Ok items
                        Ok (_, extraStar :: _) ->
                            Err
                                ( TooManyStarLines
                                    { row = extraStar.row, col = 0 }
                                )


parse : String -> String -> Result ParseErr World
parse comment textWorld =
    let
        allLines : List String
        allLines = split textWorld

        -- (grLines, stLines) : (List Line, List Line)
        (grLines, stLines) = separateLineTypes allLines
        -- TODO: error on unrecognised line

        charItems : Result ParseErr (List (List CharItem))
        charItems = parseGridLines grLines

        starLines : Result ParseErr (List StarLine)
        starLines = makeStarLines stLines

        items : Result ParseErr (List (List Items))
        items = integrateStarLines charItems starLines

        grid =
              items
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


isStarLine : Line -> Bool
isStarLine line =
    String.left 3 line.content == ":*="


notStarLine : Line -> Bool
notStarLine line =
    not (isStarLine line)


separateLineTypes : List String -> (List Line, List Line)
separateLineTypes lines =
    let
        numLines = List.indexedMap makeLine lines
        grLines = List.filter notStarLine numLines
        stLines = List.filter isStarLine numLines
    in
        (grLines, stLines)


makeStarLines : List Line -> Result ParseErr (List StarLine)
makeStarLines lines =
    resultCombine (List.map makeStarLine lines)


parseGridLines : List Line -> Result ParseErr (List (List CharItem))
parseGridLines lines =
    resultCombine (List.map parseGridLine lines)


makeStarLine : Line -> Result ParseErr StarLine
makeStarLine line =
    if (String.left 3 line.content) == ":*=" then
        Ok (StarLine line.row (String.toList (String.dropLeft 3 line.content)))
    else
        Err ( StarLineDidNotStartWithColonStarEquals
                { row = line.row, col = 0 }
                line.content
            )


parseGridLine : Line -> Result ParseErr (List CharItem)
parseGridLine line =
    resultCombine
        (List.indexedMap
            (toCharItem Nothing line.row)
            (String.toList line.content)
        )


{- From https://github.com/circuithub/elm-result-extra -}
{-| Combine a list of results into a single result (holding a list).
-}
resultCombine : List (Result x a) -> Result x (List a)
resultCombine = List.foldr (Result.map2 (::)) (Ok [])
