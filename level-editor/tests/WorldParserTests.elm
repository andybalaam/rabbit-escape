module WorldParserTests exposing (all)

import Test exposing (describe,test,Test)
import Expect


import World exposing
    ( World
    , Block(..)
    , BlockMaterial(..)
    , BlockShape(..)
    , makeBlockGrid
    , makeWorld
    )
import WorldParser exposing (parse)


all : Test
all =
    describe "Tests of the world parser and manipulation"
        [ test "Parse empty world" parseEmptyWorld
        , test "Parse world with blocks" parseWorldWithBlocks
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


-- ---

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
