module UpdateTests exposing (all)

import Test exposing (describe,test,Test)
import Expect


import Model exposing (Model, UiMode(..), UiState)
import Msg exposing (Msg(..))
import Update exposing (update)
import World exposing (Block(..), BlockMaterial(..), BlockShape(..), World)
import WorldParser exposing (parse, parseErrToString)


all : Test
all =
    describe "Tests of the update function"
        [ testActions "Clicking empty square adds block"
            ( [ "####"
              , "#  #"
              , "# r#"
              , "####"
              ]
            , { mode = InitialMode, block = Nothing }
            )
            [ ( (LevelClick 2 1)
              , [ "####"
                , "# ##"
                , "# r#"
                , "####"
                ]
              , { mode = InitialMode, block = Nothing }
              )
            ]

        , testActions "Clicking full replaces with chosen block"
            ( [ "####"
              , "#  #"
              , "# r#"
              , "####"
              ]
            , { mode = PlaceBlockMode
              , block = Just (Block Earth UpRight)
              }
            )
            [ ( (LevelClick 0 1)
              , [ "####"
                , "/  #"
                , "# r#"
                , "####"
                ]
              , { mode = PlaceBlockMode
                , block = Just (Block Earth UpRight)
                }
              )
            ]

        , testActions "ChangeMode changes mode"
            ( [ "" ]
            , { mode = InitialMode, block = Nothing }
            )
            [ ( (ChangeMode ChooseBlockMode)
              , [ "" ]
              , { mode = ChooseBlockMode, block = Nothing }
              )
            ]

        , testActions "Changing to code mode stores code text"
            ( [ "#r#" ]
            , { mode = InitialMode, block = Nothing }
            )
            [ ( (ChangeMode (CodeMode ""))  -- Even though we sent nothing
              , [ "#r#" ]
              , { mode = CodeMode "#r#", block = Nothing }
                -- We get a model
              )
            ]

        , testActions "Choosing a block updates block and mode"
            ( [ "" ]
            , { mode = InitialMode, block = Nothing }
            )
            [ ( (ChangeBlock (Block Earth UpRight))
              , [ "" ]
              , { mode = PlaceBlockMode
                , block = Just (Block Earth UpRight)
                }
              )
            ]

        , testActions "Can undo an action"
            (
                [ "   "
                , "   "
                ]
              , { mode = InitialMode, block = Nothing }
            )
            [ ( (LevelClick 2 0)
              , [ "  #"
                , "   "
                ]
              , { mode = InitialMode, block = Nothing }
              )
            , ( Undo
              , [ "   "
                , "   "
                ]
              , { mode = InitialMode, block = Nothing }
              )
            ]
        ]


parseFixed : List String -> World
parseFixed textLines =
    case parse "" (String.join "\n" textLines) of
        Err e -> Debug.crash (parseErrToString e)
        Ok w -> w


testActions :
    String ->
    ( List String, UiState ) ->
    List (Msg, List String, UiState) ->
    Test
testActions desc (initWorld, initState) msgsAndWorlds =
    let
        t = \x -> x

        parseWorld : (Msg, List String, UiState) -> (Msg, Model)
        parseWorld (msg, lines, state) =
            (msg, {world = parseFixed lines, uiState = state, t = t, past = []})

        initModel : Model
        initModel =
            { world = parseFixed initWorld
            , uiState = initState
            , t = t
            , past = []
            }

        msgsAndModels : List (Msg, Model)
        msgsAndModels =
            List.map parseWorld msgsAndWorlds

    in
        describe desc (expectUpdateGives 1 initModel msgsAndModels)


expectEqualWithoutHistory :
    (Model, Cmd Msg) ->
    (Model, Cmd Msg) ->
    () ->
    Expect.Expectation
expectEqualWithoutHistory (m1, c1) (m2, c2) =
    let
        removeHistory : Model -> Model
        removeHistory m =
            { m | past = [] }

    in
        \() -> Expect.equal (removeHistory m1, c1) (removeHistory m2, c2)


expectUpdateGives : Int -> Model -> List (Msg, Model) -> List (Test)
expectUpdateGives num initModel msgsAndModels =
    case msgsAndModels of
        [] ->
            []
        (msg, expectedModel) :: mms ->
            let
                (newModel, newCmd) = update msg initModel

                headTest : Test
                headTest =
                    test
                        ( "Step " ++ (toString num) )
                        ( expectEqualWithoutHistory
                            ( expectedModel, Cmd.none )
                            ( newModel, newCmd )
                        )
            in
                headTest :: expectUpdateGives (num + 1) newModel mms
