port module Main exposing (main)

import Test exposing (describe,Test)
import Test.Runner.Node exposing (run)
import Json.Encode exposing (Value)


import UpdateTests
import WorldParserTests
import WorldRoundTripTests
import WorldTextRenderTests


all : Test
all =
    describe "All tests"
        [ UpdateTests.all
        , WorldParserTests.all
        , WorldRoundTripTests.all
        , WorldTextRenderTests.all
        ]


main : Test.Runner.Node.TestProgram
main =
    run emit all


port emit : ( String, Value ) -> Cmd msg
