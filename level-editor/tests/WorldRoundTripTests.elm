module WorldRoundTripTests exposing (all)

import Test exposing (describe,test,Test)
import Expect


import World exposing (World)
import WorldParser exposing (ParseErr, parse, parseErrToString)
import WorldTextRender exposing (render)


all : Test
all =
    describe "Tests for round-trips of worlds as text"
        [ test "Just blocks" justBlocks
        , test "Slopes" slopes
        , test "Bridges" bridges
        , test "Metal" metal
        ]


-- ---


parseLines : String -> List String -> Result ParseErr World
parseLines comment strings =
    parse comment ((String.join "\n" strings) ++ "\n")


renderToLines : World -> List String
renderToLines world =
    String.split "\n" (render world)


roundTrips : List String -> () -> Expect.Expectation
roundTrips text =
    \() ->
        case parseLines "test" text of
            Ok world -> Expect.equal text (renderToLines world)
            Err error ->
                Expect.fail
                    ( "Parsing failed: "
                    ++ ( parseErrToString error )
                    )


-- ---


justBlocks : () -> Expect.Expectation
justBlocks =
    roundTrips
        [ "   "
        , "#  "
        , " ##"
        ]


slopes : () -> Expect.Expectation
slopes =
    roundTrips
        [ "  /"
        , "#\\ "
        , " ##"
        ]


bridges : () -> Expect.Expectation
bridges =
    roundTrips
        [ "/ ("
        , "#) "
        , " ##"
        ]


metal : () -> Expect.Expectation
metal =
    roundTrips
        [ "/ ("
        , "#) "
        , " MM"
        ]
