module UpdateTests exposing (all)

import Test exposing (describe,test,Test)
import Expect


import Model exposing (Model, UiMode(..), UiState)
import Msg exposing (Msg(..))
import Update exposing (update)
import World exposing (Block(..), BlockMaterial(..), BlockShape(..), World)
import WorldParser exposing (parse)


all : Test
all =
    describe "Tests of the update function"
        [ test "Clicking empty square adds block" clickEmptySquareAddsBlock
        , test "Clicking full replaces with chosen block" clickFullReplaces
        , test "ChangeMode changes mode" changeModeChangesMode
        , test "Changing to code mode stores code text" codeModeStoresText
        , test "Choosing a block updates block and mode" choosingBlockUpdates
        ]


parseFixed : String -> World
parseFixed textWorld =
    case parse "" textWorld of
        Err s -> Debug.crash s
        Ok w -> w


emptyModel : Model
emptyModel =
    { world = parseFixed ""
    , uiState = { mode = InitialMode, block = Nothing }
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


clickFullReplaces : () -> Expect.Expectation
clickFullReplaces =
    \() ->
        Expect.equal
            (
                { emptyModel
                | world = parseFixed "####\n/  #\n# r#\n####"
                , uiState =
                    { mode = PlaceBlockMode
                    , block = Just (Block Earth UpRight)
                    }
                }
            , Cmd.none
            )
            ( update
                (LevelClick 0 1)
                (
                    { emptyModel
                    | world = parseFixed "####\n#  #\n# r#\n####"
                    , uiState =
                        { mode = PlaceBlockMode
                        , block = Just (Block Earth UpRight)
                        }
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


codeModeGensText : () -> Expect.Expectation
codeModeGensText =
    let
       uiState = emptyModel.uiState
    in
        \() ->
            Expect.equal
                (
                    { emptyModel
                    | uiState = { uiState | mode = CodeMode "#r#"}
                    }
                , Cmd.none
                )
                ( update
                    (ChangeMode (CodeMode "#r#"))
                    emptyModel
                )


choosingBlockUpdates : () -> Expect.Expectation
choosingBlockUpdates =
    let
       uiState = emptyModel.uiState
    in
        \() ->
            Expect.equal
                (
                    { emptyModel
                    | uiState =
                        { uiState
                        | mode = PlaceBlockMode
                        , block = Just (Block Earth UpRight)
                        }
                    }
                , Cmd.none
                )
                ( update
                    (ChangeBlock (Block Earth UpRight))
                    emptyModel
                )
