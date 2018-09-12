module WorldParser exposing
    ( Items
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


import Regex


import MetaLines exposing
    ( MetaLines
    , MetaValue(..)
    )
import ParseErr exposing (ParseErr(..))
import Rabbit exposing (Rabbit, movedRabbit)
import World exposing
    ( Block(..)
    , BlockMaterial(..)
    , BlockShape(..)
    , Grid
    , World
    , makeBlockGrid
    , makeWorld
    )
import Thing exposing (Thing)


import Item2Text exposing
    ( CharItem(..)
    , Pos
    , charToBlock
    , charToRabbit
    , charToThing
    , toText
    )


type alias Items =
    { block : Block
    , rabbits : List Rabbit
    , things : List Thing
    }


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
    ++ (String.fromInt (pos.row + 1))
    ++ ", column "
    ++ (String.fromInt (pos.col + 1))
    ++ ": "
    )


parseErrToString : ParseErr -> String
parseErrToString e =
    case e of
        TwoBlocksInOneStarPoint pos c1 c2 ->
            ( posToString pos
            ++ "Two blocks in one startpoint: '"
            ++ (String.fromChar c1)
            ++ "' and '"
            ++ (String.fromChar c2)
            ++ "'."
            )
        StarInsideStarPoint pos ->
            ( posToString pos
            ++ "Star inside a star point."
            )
        UnrecognisedChar pos ch ->
            ( posToString pos
            ++ "Unrecognised character: '"
            ++ (String.fromChar ch)
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
        LineWrongLength pos exp act ->
            ( posToString pos
            ++ "The lines of this level are different lengths - they must be "
            ++ "all exactly the same length.  The first line was "
            ++ String.fromInt exp
            ++ " characters long, but this one is "
            ++ String.fromInt act
            ++ "."
            )
        MetaParseFailure pos err name value ->
            ( posToString pos
            ++ "Failed to parse meta property with name '"
            ++ name
            ++ "' and value '"
            ++ value
            ++ "'.  The error is: "
            ++ err
            )
        UnknownMetaName pos name value ->
            ( posToString pos
            ++ "Unknown meta property name '"
            ++ name
            ++ "'.  (The value provided was '"
            ++ value
            ++ "')."
            )


blockToText : Block -> Char
blockToText block =
    Tuple.first (toText block [] [])


addRabbitCoords : Pos -> Rabbit -> Rabbit
addRabbitCoords pos rabbit =
    movedRabbit pos.col pos.row rabbit


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
                        case charToThing c of
                        Just thing ->
                            Ok
                                (ThingChar
                                    literalPos
                                    (Thing.moved pos.col pos.row thing)
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
            Ok { items | rabbits = items.rabbits ++ [charRabbit] }
        ThingChar pos charThing ->
            Ok { items | things = items.things ++ [charThing] }
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
        noItems = Ok { block = NoBlock, rabbits = [], things = [] }
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
                ( { block = b, rabbits = [], things = [] }
                , starLines
                )
        RabbitChar pos r ->
            Ok
                ( { block = NoBlock
                  , rabbits = [(addRabbitCoords pos r)]
                  , things = []
                  }
                , starLines
                )
        ThingChar pos t ->
            Ok
                ( { block = NoBlock
                  , rabbits = []
                  , things = [Thing.moved pos.col pos.row t]
                  }
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
        _ -> Ok ([], starLines)


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
        _ -> Ok ([], starLines)


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
                        Ok (its, []) ->  -- No extra star lines - all good
                            Ok its
                        Ok (_, extraStar :: _) ->
                            Err
                                ( TooManyStarLines
                                    { row = extraStar.row, col = 0 }
                                )


-- Check whether a grid of items has the right length lines,
-- and return it if so.  If not, return a ParseErr.
itemsIfValid : List (List Items) -> Result ParseErr (List (List Items))
itemsIfValid itemsGrid =
    let
        firstLineLen =
            case List.head itemsGrid of
                Just firstLine -> List.length firstLine
                Nothing -> -1

        lineIfValid : Int -> Int -> List Items -> Result ParseErr (List Items)
        lineIfValid requiredLen row line =
            let
                len = List.length line
            in
                if len == requiredLen then
                    Ok line
                else
                    let
                        col =
                            if len > requiredLen then
                                requiredLen
                            else
                                len - 1
                    in
                        Err (LineWrongLength {row=row, col=col} requiredLen len)
    in
        resultCombine (List.indexedMap (lineIfValid firstLineLen) itemsGrid)


parse : String -> String -> Result ParseErr World
parse comment textWorld =
    let
        allLines : List String
        allLines = split textWorld

        -- (grLines, stLines, metaLines) : (List Line, List Line, List Line)
        (grLines, stLines, meLines) = separateLineTypes allLines

        charItems : Result ParseErr (List (List CharItem))
        charItems = parseGridLines grLines

        starLines : Result ParseErr (List StarLine)
        starLines = makeStarLines stLines

        rawItems : Result ParseErr (List (List Items))
        rawItems = integrateStarLines charItems starLines

        items : Result ParseErr (List (List Items))
        items = case rawItems of
            Ok a -> itemsIfValid a
            e -> e

        blocks : Result ParseErr (List (List Block))
        blocks = Result.map (List.map (List.map .block)) items

        grid : Result ParseErr (Grid Block)
        grid = Result.map makeBlockGrid blocks

        rabbits : Result ParseErr (List Rabbit)
        rabbits =
            items                              -- List (List Items)
            |> Result.map List.concat          -- List Items
            |> Result.map (List.map .rabbits)  -- List (List Rabbit)
            |> Result.map List.concat          -- List Rabbit

        things : Result ParseErr (List Thing)
        things =
            items                              -- List (List Items)
            |> Result.map List.concat          -- List Items
            |> Result.map (List.map .things)   -- List (List Thing)
            |> Result.map List.concat          -- List Thing

        metaLines : Result ParseErr MetaLines
        metaLines = makeMetaLines meLines
    in
        Result.map4 (makeWorld comment) grid rabbits things metaLines


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


isMetaLine : Line -> Bool
isMetaLine line =
    String.left 1 line.content == ":"


separateLineTypes : List String -> (List Line, List Line, List Line)
separateLineTypes rawLines =
    let
        lines = List.indexedMap makeLine rawLines
        (stLines, otherLines) = List.partition isStarLine lines
        (meLines, grLines) = List.partition isMetaLine otherLines
    in
        (grLines, stLines, meLines)


makeStarLines : List Line -> Result ParseErr (List StarLine)
makeStarLines lines =
    resultCombine (List.map makeStarLine lines)


makeMetaLines : List Line -> Result ParseErr MetaLines
makeMetaLines lines =
    List.foldr addMetaLine (Ok MetaLines.defaults) lines


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


addMetaProperty
    :  Int
    -> String
    -> String
    -> MetaLines
    -> Result ParseErr MetaLines
addMetaProperty row name value existing =
    case MetaLines.parseAndSet name value existing of
        Ok metaLines ->
            Ok metaLines
        Err (MetaLines.UnknownName _) ->
            Err
                ( UnknownMetaName
                    {col=0, row=row}
                    name
                    value
                )
        Err (MetaLines.BadValue _ _) ->
            Err
                ( MetaParseFailure
                    {col=0, row=row}
                    "Should be a number!" -- Will need more if not just ints
                    name
                    value
                )


equalsSign : Regex.Regex
equalsSign =
    Maybe.withDefault Regex.never (Regex.fromString "=")


addMetaLine : Line -> Result ParseErr MetaLines -> Result ParseErr MetaLines
addMetaLine line acc =
    let
        withoutColon : String
        withoutColon = String.dropLeft 1 line.content

        keyValue : List String
        keyValue = Regex.splitAtMost 1 equalsSign withoutColon

        (propertyName, propertyValue) =
            case keyValue of
                [k, v] -> (k, v)
                k :: t -> (k, String.concat t)
                [] -> ("", "")
    in
        acc |> Result.andThen
            (addMetaProperty line.row propertyName propertyValue)


{- From https://github.com/circuithub/elm-result-extra -}
{-| Combine a list of results into a single result (holding a list).
-}
resultCombine : List (Result x a) -> Result x (List a)
resultCombine = List.foldr (Result.map2 (::)) (Ok [])
