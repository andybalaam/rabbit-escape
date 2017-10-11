module WorldParserTests exposing (all)

import Test exposing (describe,test,Test)
import Expect


import Item2Text exposing (CharItem(..))
import Rabbit exposing (Direction(..), makeRabbit)
import World exposing
    ( World
    , Block(..)
    , BlockMaterial(..)
    , BlockShape(..)
    , makeBlockGrid
    , makeWorld
    )
import WorldParser exposing
    ( Items
    , ParseErr(..)
    , StarLine
    , integrateSquare
    , integrateLine
    , resultCombine
    , mergeNewCharIntoItems
    , parse
    , parseErrToString
    , makeStarLine
    , starLineToItems
    , toCharItem
    )


all : Test
all =
    describe "Tests of the world parser and manipulation"
        [ test "Combining good items make a good list" combiningGood
        , test "Combining a bad items makes a bad result" combiningBad
        , test "Combining no items makes a good empty list" combiningNone
        , toCharItemCases
        , mergeNewCharIntoItemsCases
        , starLineToItemsCases
        , integrateSquareCases
        , integrateLineCases
        , test "Parse empty world" parseEmptyWorld
        , test "Parse world with blocks" parseWorldWithBlocks
        , test "Parse world with rabbits" parseWorldWithRabbits
--        , test "Parse overlapping rabbits" parseOverlappingRabbits
        ]


-- ---


okAndEqual : Result ParseErr a -> a -> () -> Expect.Expectation
okAndEqual actual expected =
    \() ->
        case actual of
            Ok value -> Expect.equal value expected
            Err error -> Expect.fail ( parseErrToString error )


parseLines : String -> List String -> Result ParseErr World
parseLines comment strings =
    parse comment ((String.join "\n" strings) ++ "\n")


fltErth : Block
fltErth =
    Block Earth Flat


uprErth : Block
uprErth =
    Block Earth UpRight


fltMetl : Block
fltMetl =
    Block Metal Flat


-- Combine --


combiningGood : () -> Expect.Expectation
combiningGood =
    \() ->
        Expect.equal
            (Ok ["a", "b", "c"])
            (resultCombine [Ok "a", Ok "b", Ok "c"])


combiningBad : () -> Expect.Expectation
combiningBad =
    \() ->
        Expect.equal
            (Err "xxx")
            (resultCombine [Err "xxx", Ok "b", Ok "c"])


combiningNone : () -> Expect.Expectation
combiningNone =
    \() ->
        Expect.equal
            (Ok [])
            (resultCombine [])

-- Merge --


emptyItems : Items
emptyItems = { block = NoBlock, rabbits = [] }


toCharItemCases : Test
toCharItemCases =
    let
        pos14 = { row = 4, col = 1 }
        pos36 = { row = 6, col = 3 }
        rabr14 = makeRabbit 1 4 Right
        rabl36 = makeRabbit 3 6 Left
        n = Nothing
        t desc gridPos pos char exp =
            test
                desc
                ( \() ->
                    Expect.equal
                        ( toCharItem gridPos pos.row pos.col char )
                        ( exp )
                )
    in
        describe "toCharItem"
            [ t "Sloping block" n pos14 '/' ( Ok (BlockChar pos14 uprErth ) )
            , t "Flat metal"    n pos36 'M' ( Ok (BlockChar pos36 fltMetl ) )
            , t "Right rabbit"  n pos14 'r' ( Ok (RabbitChar pos14 rabr14 ) )
            , t "Left rabbit"   n pos36 'j' ( Ok (RabbitChar pos36 rabl36 ) )
            , t "Star"          n pos14 '*' ( Ok ( StarChar pos14 ) )
            , t "Unknown" n pos14 '>' ( Err ( UnrecognisedChar pos14 '>' ) )

            , t "Rabbit at a different position"
               (Just pos36) pos14 'j' ( Ok (RabbitChar pos14 rabl36 ) )
            ]


mergeNewCharIntoItemsCases : Test
mergeNewCharIntoItemsCases =
    let
        mrg ch items = mergeNewCharIntoItems ch ( Ok items )
        blc = fltErth
        rab = makeRabbit 3 0 Right
        ra2 = makeRabbit 3 1 Left
        pos13 = { row = 3, col = 1 }
        pos24 = { row = 4, col = 2 }
        pos36 = { row = 6, col = 3 }
        staPos = { row = 4, col = 0 }
        rabPos = { row = rab.y, col = rab.x }
        ra2Pos = { row = ra2.y, col = ra2.x }
        blcCh = BlockChar pos24 blc
        rabCh = RabbitChar rabPos rab
        ra2Ch = RabbitChar ra2Pos ra2
        staCh = StarChar staPos
        err = Err ( TwoBlocksInOneStarPoint pos36 '#' '#')
        t desc act exp = test desc (\() -> Expect.equal act exp)
    in
        describe "mergeNewCharIntoItems"
            [ t "Block merges into empty"
                ( mrg blcCh emptyItems )
                ( Ok { block = fltErth, rabbits = [] } )

            , t "Rabbit merges into empty"
                ( mrg rabCh emptyItems )
                ( Ok { block = NoBlock, rabbits = [rab] } )

            , t "Block won't merge over a block"
                ( mrg blcCh { emptyItems | block = fltMetl } )
                ( Err ( TwoBlocksInOneStarPoint pos24 'M' '#' ) )

            , t "Rabbit merges into block"
                ( mrg rabCh { emptyItems | block = fltMetl } )
                ( Ok { block = fltMetl, rabbits = [rab] } )

            , t "Rabbit merges into block"
                ( mrg ra2Ch { block = fltMetl, rabbits = [rab] } )
                ( Ok { block = fltMetl, rabbits = [rab, ra2] } )

            , t "Errors propagate"
                ( mergeNewCharIntoItems ra2Ch err )
                ( err )

            , t "Star in starline is an error"
                ( mrg staCh emptyItems )
                ( Err ( StarInsideStarPoint staPos ) )
            ]


starLineToItemsCases : Test
starLineToItemsCases =
    let
        pos56 = { row = 6, col = 5 }
        pos59 = { row = 9, col = 5 }
        pos69 = { row = 9, col = 6 }
        rabl36 = makeRabbit 3 6 Left
        rabr45 = makeRabbit 4 5 Right
        rabr46 = makeRabbit 4 6 Right
        rabl56 = makeRabbit 5 6 Left
        rabr56 = makeRabbit 5 6 Right
        makeStarLineOk line =
            case makeStarLine line of
                Ok s -> s
                default -> Debug.crash "Starline failed to parse"
        t desc inp row pos exp =
            test
                desc
                ( \() ->
                    Expect.equal
                        ( starLineToItems
                            ( makeStarLineOk { row = row, content = inp } )
                            pos
                        )
                        exp
                )
    in
        describe "starLineToItems"
            [ t "Single block"
                ":*=#" 9 pos56
                (Ok {block=fltErth, rabbits=[]})

            , t "Block and rabbit"
                ":*=/r" 9 pos56
                (Ok {block=uprErth, rabbits=[rabr56]})

            , t "Rabbits"
                ":*=jr" 9 pos56
                (Ok {block=NoBlock, rabbits=[rabl56, rabr56]})

            , t "Rabbits and block" ":*=jr#" 9 pos56
                (Ok {block=fltErth, rabbits=[rabl56, rabr56]})

            , t "Unknown char"
                ":*=jr#>" 9 pos56
                (Err (UnrecognisedChar pos69 '>'))

            , t "Star in star"
                ":*=jr*#" 9 pos56
                (Err (StarInsideStarPoint pos59))
            ]


integrateSquareCases : Test
integrateSquareCases =
    let
        pos11 = { row =  1, col = 1 }
        pos25 = { row =  5, col = 2 }
        pos88 = { row =  8, col = 8 }
        pos34 = { row =  4, col = 3 }
        pos39 = { row =  9, col = 3 }
        pos3a = { row = 10, col = 3 }
        blcErth11 = BlockChar pos11 fltErth
        rab25 = (makeRabbit 2 5 Right)
        rab34 = (makeRabbit 3 4 Right)
        rabCh25 = RabbitChar pos25 rab25
        star34 = StarChar pos34
        remainingStarLines =
            [ StarLine 9 (String.toList "rrj")
            ]
        starLines = StarLine 8 (String.toList "/r") :: remainingStarLines
        t desc act exp = test desc (\() -> Expect.equal act exp)
    in
        describe "integrateSquare"
            [ t "Block is left alone"
                (integrateSquare blcErth11 starLines)
                (Ok ({block=fltErth, rabbits=[]}, starLines))

            , t "Rabbit is left alone"
                (integrateSquare rabCh25 starLines)
                (Ok ({block=NoBlock, rabbits=[rab25]}, starLines))

            , t "Star integrates first starpoint"
                (integrateSquare star34 starLines)
                (Ok ({block = uprErth, rabbits=[rab34]}, remainingStarLines))

            , t "Bad starline produces error"
                (integrateSquare star34 [ StarLine 3 ['*']])
                (Err (StarInsideStarPoint { row = 3, col = 3 }))
            ]


integrateLineCases : Test
integrateLineCases =
    let
        pos01 = { row = 1, col = 0 }
        pos11 = { row = 1, col = 1 }
        pos21 = { row = 1, col = 2 }
        pos22 = { row = 2, col = 2 }
        starLine1 = StarLine 10 (String.toList "rrj")
        starLine2 = StarLine 11 (String.toList "Mj")
        starLine3 = StarLine 12 (String.toList "#rj")
        starLines = [starLine1, starLine2, starLine3]
        rabr01 = makeRabbit 0 1 Right
        rabl01 = makeRabbit 0 1 Left
        rabl11 = makeRabbit 1 1 Left
        rabr21 = makeRabbit 2 1 Right
        rabl21 = makeRabbit 2 1 Left
        rabr22 = makeRabbit 2 2 Right
        chErth = BlockChar pos11 fltErth
        chRabb = RabbitChar pos11 rabl11
        t desc act exp = test desc (\() -> Expect.equal act exp)
    in
        describe "integrateLine"
            [ t "Non-stars are left alone"
                (integrateLine [chErth, chRabb, chErth] starLines)
                ( Ok
                    (
                        [ { block=fltErth, rabbits=[] }
                        , { block=NoBlock, rabbits=[rabl11] }
                        , { block=fltErth, rabbits=[] }
                        ]
                    , starLines
                    )
                )

            , t "Star gets integrated"
                (integrateLine [StarChar pos01, chRabb, chErth] starLines)
                ( Ok
                    (
                        [ { block=NoBlock, rabbits=[rabr01, rabr01, rabl01] }
                        , { block=NoBlock, rabbits=[rabl11] }
                        , { block=fltErth, rabbits=[] }
                        ]
                    , [starLine2, starLine3]
                    )
                )

            , t "Multiple stars get integrated"
                (integrateLine
                    [StarChar pos01, StarChar pos11, StarChar pos21]
                    starLines
                )
                ( Ok
                    (
                        [ { block=NoBlock, rabbits=[rabr01, rabr01, rabl01] }
                        , { block=fltMetl, rabbits=[rabl11] }
                        , { block=fltErth, rabbits=[rabr21, rabl21] }
                        ]
                    , []
                    )
                )

            , t "Bad star produces an error"
                (integrateLine
                    [StarChar pos01, chErth, chErth]
                    [StarLine 10 (String.toList "MM")] -- two blocks
                )
                ( Err
                    ( TwoBlocksInOneStarPoint { row = 10, col = 4 } 'M' 'M' )
                )
            ]


-- Parse --


parseEmptyWorld : () -> Expect.Expectation
parseEmptyWorld =
    okAndEqual
        (parseLines
            "tst"
            [ "   "
            , "   "
            , "   "
            ]
        )
        (makeWorld
            "tst"
            (makeBlockGrid
                [ [NoBlock, NoBlock, NoBlock]
                , [NoBlock, NoBlock, NoBlock]
                , [NoBlock, NoBlock, NoBlock]
                ]
            )
            []
        )


parseWorldWithBlocks : () -> Expect.Expectation
parseWorldWithBlocks =
    okAndEqual
        (parseLines
            "tst"
            [ "    "
            , "   #"
            , "    "
            , "####"
            ]
        )
        (makeWorld
            "tst"
            (makeBlockGrid
                [ [NoBlock, NoBlock, NoBlock, NoBlock]
                , [NoBlock, NoBlock, NoBlock, fltErth]
                , [NoBlock, NoBlock, NoBlock, NoBlock]
                , [fltErth, fltErth, fltErth, fltErth]
                ]
            )
            []
        )


parseWorldWithRabbits : () -> Expect.Expectation
parseWorldWithRabbits =
    okAndEqual
        (parseLines
            "tst"
            [ "   j"
            , "   #"
            , "r   "
            , "####"
            ]
        )
        (makeWorld
            "tst"
            (makeBlockGrid
                [ [NoBlock, NoBlock, NoBlock, NoBlock]
                , [NoBlock, NoBlock, NoBlock, fltErth]
                , [NoBlock, NoBlock, NoBlock, NoBlock]
                , [fltErth, fltErth, fltErth, fltErth]
                ]
            )
            [ makeRabbit 3 0 Left
            , makeRabbit 0 2 Right
            ]
        )


parseOverlappingRabbits : () -> Expect.Expectation
parseOverlappingRabbits =
    okAndEqual
        (parseLines
            "tst"
            [ "   *"
            , "   #"
            , "    "
            , "####"
            , ":*=rj"
            ]
        )
        (makeWorld
            "tst"
            (makeBlockGrid
                [ [NoBlock, NoBlock, NoBlock, NoBlock]
                , [NoBlock, NoBlock, NoBlock, fltErth]
                , [NoBlock, NoBlock, NoBlock, NoBlock]
                , [fltErth, fltErth, fltErth, fltErth]
                ]
            )
            [ makeRabbit 3 0 Right
            , makeRabbit 3 0 Left
            ]
        )
