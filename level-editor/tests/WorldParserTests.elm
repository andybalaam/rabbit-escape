module WorldParserTests exposing (all)

import Test exposing (describe,test,Test)
import Expect


import Item2Text exposing (SingleCharItems(..))
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
    )


all : Test
all =
    describe "Tests of the world parser and manipulation"
        [ test "Combining good items make a good list" combiningGood
        , test "Combining a bad items makes a bad result" combiningBad
        , test "Combining no items makes a good empty list" combiningNone
        , mergeNewCharIntoItemsCases
        , test "Parse empty world" parseEmptyWorld
        , test "Parse world with blocks" parseWorldWithBlocks
        , test "Parse world with rabbits" parseWorldWithRabbits
--        , test "Parse overlapping rabbits" parseOverlappingRabbits
        ]


-- ---


okAndEqual : Result String a -> a -> () -> Expect.Expectation
okAndEqual actual expected =
    \() ->
        case actual of
            Ok value -> Expect.equal value expected
            Err error -> Expect.fail error


parseLines : String -> List String -> Result String World
parseLines comment strings =
    parse comment ((String.join "\n" strings) ++ "\n")


fltErth : Block
fltErth =
    Block Earth Flat


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


mergeNewCharIntoItemsCases : Test
mergeNewCharIntoItemsCases =
    let
        mrg pos ch items = mergeNewCharIntoItems pos ch ( Ok items )
        blc = fltErth
        rab = makeRabbit 3 0 Right
        ra2 = makeRabbit 3 1 Left
        blcCh = BlockChar blc
        rabCh = RabbitChar rab
        ra2Ch = RabbitChar ra2
        staCh = StarChar
        err = Err ( TwoBlocksInOneStarPoint (3, 6) '#' '#')
        t desc act exp = test desc (\() -> Expect.equal act exp)
    in
        describe "mergeNewCharIntoItems"
            [ t "Block merges into empty"
                ( mrg (2, 4) blcCh emptyItems )
                ( Ok { block = fltErth, rabbits = [] } )

            , t "Rabbit merges into empty"
                ( mrg (2, 4) rabCh emptyItems )
                ( Ok { block = NoBlock, rabbits = [rab] } )

            , t "Block won't merge over a block"
                ( mrg (2, 4) blcCh { emptyItems | block = fltMetl } )
                ( Err ( TwoBlocksInOneStarPoint (2, 4) 'M' '#' ) )

            , t "Rabbit merges into block"
                ( mrg (2, 4) rabCh { emptyItems | block = fltMetl } )
                ( Ok { block = fltMetl, rabbits = [rab] } )

            , t "Rabbit merges into block"
                ( mrg (2, 4) ra2Ch { block = fltMetl, rabbits = [rab] } )
                ( Ok { block = fltMetl, rabbits = [rab, ra2] } )

            , t "Errors propagate"
                ( mergeNewCharIntoItems (2, 4) ra2Ch err )
                ( err )

            , t "Star in starline is an error"
                ( mrg (1, 3) staCh emptyItems )
                ( Err ( StarInsideStarPoint (1, 3) ) )
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
