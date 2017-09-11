module UpdateTests exposing (all)

import Test exposing (describe,test,Test)
import Expect


import Model exposing (Model, ModelScreen, UiMode(..), UiState)
import Msg exposing (Msg(..))
import Units exposing (Pixels(..))
import Update exposing (update)
import World exposing (Block(..), World)
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


emptyModel : Model
emptyModel =
    { screen = { width = Pixels 0, height = Pixels 0 }
    , world = parseFixed ""
    , uiState = { mode = InitialMode, block = NoBlock }
    }


resizingAffectsScreenSize : () -> Expect.Expectation
resizingAffectsScreenSize =
    \() ->
        Expect.equal
            (
                { emptyModel
                | screen = { width = Pixels 23, height = Pixels 45 }
                }
            , Cmd.none
            )
            (update (Resize 23 45) emptyModel)


clickEmptySquareAddsBlock : () -> Expect.Expectation
clickEmptySquareAddsBlock =
    \() ->
        Expect.equal
            (
                { emptyModel
                | world = parseFixed "####\n# ##\n# r#\n####"
                }
            , Cmd.none
            )
            ( update
                (LevelClick 2 1)
                (
                    { emptyModel
                    | world = parseFixed "####\n#  #\n# r#\n####"
                    }
                )
            )
