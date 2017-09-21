module UpdateTests exposing (all)

import Test exposing (describe,test,Test)
import Expect


import Model exposing (Model, UiMode(..), UiState)
import Msg exposing (Msg(..))
import Update exposing (update)
import World exposing (Block(..), World)
import WorldParser exposing (parse)


all : Test
all =
    describe "Tests of the update function"
        [ test "Clicking empty square adds block" clickEmptySquareAddsBlock
        , test "ChangeMode changes mode" changeModeChangesMode
        ]


parseFixed : String -> World
parseFixed textWorld =
    case parse "" textWorld of
        Err s -> Debug.crash s
        Ok w -> w


emptyModel : Model
emptyModel =
    { world = parseFixed ""
    , uiState = { mode = InitialMode, block = NoBlock }
    }


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


changeModeChangesMode : () -> Expect.Expectation
changeModeChangesMode =
    let
       uiState = emptyModel.uiState
    in
        \() ->
            Expect.equal
                (
                    { emptyModel
                    | uiState = { uiState | mode = ChooseBlockMode }
                    }
                , Cmd.none
                )
                ( update
                    (ChangeMode ChooseBlockMode)
                    emptyModel
                )
