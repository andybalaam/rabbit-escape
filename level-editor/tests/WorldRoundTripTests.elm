module WorldRoundTripTests exposing (all)

import Test exposing (describe,test,Test)
import Expect


import ParseErr exposing (ParseErr)
import World exposing (World)
import WorldParser exposing (parse, parseErrToString)
import WorldTextRender exposing (render)


all : Test
all =
    describe "Tests for round-trips of worlds as text"
        [ test "Just blocks"
            ( roundTrips
                [ "   "
                , "#  "
                , " ##"
                ]
            )

        , test "Slopes"
            ( roundTrips
                [ "  /"
                , "#\\ "
                , " ##"
                ]
            )

        , test "Bridges"
            ( roundTrips
                [ "/ ("
                , "#) "
                , " ##"
                ]
            )

        , test "Metal"
            ( roundTrips
                [ "/ ("
                , "#) "
                , " MM"
                ]
            )

        , test "Rabbits"
            ( roundTrips
                [ "/ ("
                , "#r "
                , "jMM"
                ]
            )

        , test "Starpoints"
            ( roundTrips
                [ "/ *"
                , "#* "
                , "jMM"
                , ":*=)rjrj"
                , ":*=#jj"
                ]
            )

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
