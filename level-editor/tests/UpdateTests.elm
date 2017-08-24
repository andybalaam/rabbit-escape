module UpdateTests exposing (all)

import Test exposing (describe,test,Test)
import Expect


import Model exposing (Model, ModelScreen)
import Msg exposing (Msg(..))
import Units exposing (Pixels(..))
import Update exposing (update)
import World exposing (World)
import WorldParser exposing (parse)


all : Test
all =
    describe "Tests of the update function"
        [ test "Resizing affects screen size" resizingAffectsScreenSize
        , test "Clicking empty square adds block" clickEmptySquareAddsBlock
        ]


parseFixed : String -> World
parseFixed textWorld =
    case parse "" textWorld of
        Err s -> Debug.crash s
        Ok w -> w


emptyWorld : World
emptyWorld =
    parseFixed ""


someScreen : ModelScreen
someScreen =
    { width = Pixels 2
    , height = Pixels 2
    }


resizingAffectsScreenSize : () -> Expect.Expectation
resizingAffectsScreenSize =
    \() ->
        Expect.equal
            (
                { screen = { width = Pixels 23, height = Pixels 45 }
                , world = emptyWorld
                }
            , Cmd.none
            )
            ( update
                (Resize 23 45)
                (
                    { screen = { width = Pixels 1, height = Pixels 1 }
                    , world = emptyWorld
                    }
                )
            )


clickEmptySquareAddsBlock : () -> Expect.Expectation
clickEmptySquareAddsBlock =
    \() ->
        Expect.equal
            (
                { screen = someScreen
                , world = parseFixed "####\n# ##\n# r#\n####"
                }
            , Cmd.none
            )
            ( update
                (LevelClick 2 1)
                (
                    { screen = someScreen
                    , world = parseFixed "####\n#  #\n# r#\n####"
                    }
                )
            )
