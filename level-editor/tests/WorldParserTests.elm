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
    , resultCombine
    , mergeNewCharIntoItems
    , parse
    , parseErrToString
    , toItems
    )


all : Test
all =
    describe "Tests of the world parser and manipulation"
        [ test "Combining good items make a good list" combiningGood
        , test "Combining a bad items makes a bad result" combiningBad
        , test "Combining no items makes a good empty list" combiningNone
        , toItemsCases
        , mergeNewCharIntoItemsCases
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


toItemsCases : Test
toItemsCases =
    let
        pos14 = { row = 4, col = 1 }
        pos36 = { row = 6, col = 3 }
        rabr14 = makeRabbit 1 4 Right
        rabl36 = makeRabbit 3 6 Left
        t desc pos char exp =
            test
                desc
                ( \() ->
                    Expect.equal
                        ( toItems pos.row pos.col char )
                        ( exp )
                )
    in
        describe "toItems"
            [ t "Sloping block" pos14 '/' ( Ok (BlockChar pos14 uprErth ) )
            , t "Flat metal"    pos36 'M' ( Ok (BlockChar pos36 fltMetl ) )
            , t "Right rabbit"  pos14 'r' ( Ok (RabbitChar pos14 rabr14 ) )
            , t "Left rabbit"   pos36 'j' ( Ok (RabbitChar pos36 rabl36 ) )
            , t "Star"          pos14 '*' ( Ok ( StarChar pos14 ) )
            , t "Unknown" pos14 '>' ( Err ( UnrecognisedChar pos14 '>' ) )
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
