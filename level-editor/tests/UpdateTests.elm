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

        , testActions "Undo skips actions that don't modify the world"
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
            , ( (ChangeBlock (Block Earth UpRight))
              , [ "  #"
                , "   "
                ]
              , { mode = PlaceBlockMode
                , block = Just (Block Earth UpRight)
                }
              )
            , ( Undo
              , [ "   "  -- The world goes back to before
                , "   "  -- but the mode and block stay selected.
                ]
              , { mode = PlaceBlockMode
                , block = Just (Block Earth UpRight)
                }
              )
            ]

        , testActions "Can undo multiple actions"
            (
                [ "/  "
                , "/  "
                ]
              , { mode = InitialMode, block = Nothing }
            )
            [ ( (LevelClick 2 0)
              , [ "/ #"
                , "/  "
                ]
              , { mode = InitialMode, block = Nothing }
              )
            , ( (LevelClick 1 1)
              , [ "/ #"
                , "/# "
                ]
              , { mode = InitialMode, block = Nothing }
              )
            , ( (LevelClick 2 1)
              , [ "/ #"
                , "/##"
                ]
              , { mode = InitialMode, block = Nothing }
              )
            , ( Undo
              , [ "/ #"
                , "/# "
                ]
              , { mode = InitialMode, block = Nothing }
              )
            , ( Undo
              , [ "/ #"
                , "/  "
                ]
              , { mode = InitialMode, block = Nothing }
              )
            , ( Undo
              , [ "/  "
                , "/  "
                ]
              , { mode = InitialMode, block = Nothing }
              )
            ]

        , testActions "Undoing when no history does nothing"
            (
                [ "MMM"
                , "MMM"
                ]
              , { mode = InitialMode, block = Nothing }
            )
            [ ( Undo
              , [ "MMM"
                , "MMM"
                ]
              , { mode = InitialMode, block = Nothing }
              )
            ]

        , testActions "Undoing back past the beginning does nothing"
            (
                [ "MMM"
                , "MMM"
                ]
              , { mode = InitialMode, block = Nothing }
            )
            [ ( (LevelClick 2 1)
              , [ "MMM"
                , "MM#"
                ]
              , { mode = InitialMode, block = Nothing }
              )
            , ( Undo
              , [ "MMM"
                , "MMM"
                ]
              , { mode = InitialMode, block = Nothing }
              )
            , ( Undo
              , [ "MMM"
                , "MMM"
                ]
              , { mode = InitialMode, block = Nothing }
              )
            ]

        , testActions "Redo restores an undone action"
            (
                [ "MMM"
                , "MM/"
                ]
              , { mode = InitialMode, block = Nothing }
            )
            [ ( (LevelClick 2 1)
              , [ "MMM"
                , "MM#"
                ]
              , { mode = InitialMode, block = Nothing }
              )
            , ( Undo
              , [ "MMM"
                , "MM/"
                ]
              , { mode = InitialMode, block = Nothing }
              )
            , ( Redo
              , [ "MMM"
                , "MM#"
                ]
              , { mode = InitialMode, block = Nothing }
              )
            ]

        , testActions "Doing something new removes redo stack"
            (
                [ "MMM"
                , "MM/"
                ]
              , { mode = InitialMode, block = Nothing }
            )
            [ ( (LevelClick 2 1)
              , [ "MMM"
                , "MM#"
                ]
              , { mode = InitialMode, block = Nothing }
              )
            , ( Undo
              , [ "MMM"
                , "MM/"
                ]
              , { mode = InitialMode, block = Nothing }
              )
            , ( (LevelClick 0 0)
              , [ "#MM"
                , "MM/"
                ]
              , { mode = InitialMode, block = Nothing }
              )
            , ( Redo
              , [ "#MM"  -- The redo did nothing because
                , "MM/"  -- we had placed a block in between
                ]
              , { mode = InitialMode, block = Nothing }
              )
            , ( Undo
              , [ "MMM"  -- Undo still works
                , "MM/"
                ]
              , { mode = InitialMode, block = Nothing }
              )
            , ( Redo
              , [ "#MM"  -- And redo undoes the undo
                , "MM/"
                ]
              , { mode = InitialMode, block = Nothing }
              )
            ]

        , testActions "Non-modifying actions don't affect redo"
            (
                [ "///"
                , "   "
                ]
              , { mode = InitialMode, block = Nothing }
            )
            [ ( (LevelClick 2 1)
              , [ "///"
                , "  #"
                ]
              , { mode = InitialMode, block = Nothing }
              )
            , ( Undo
              , [ "///"
                , "   "
                ]
              , { mode = InitialMode, block = Nothing }
              )
            , ( (ChangeBlock (Block Earth UpRight))
              , [ "///"
                , "   "
                ]
              , { mode = PlaceBlockMode
                , block = Just (Block Earth UpRight)
                }
              )
            , ( Redo
              , [ "///"
                , "  #"
                ]
              , { mode = PlaceBlockMode
                , block = Just (Block Earth UpRight)
                }
              )
            , ( (ChangeBlock (Block Earth UpLeft))
              , [ "///"
                , "  #"
                ]
              , { mode = PlaceBlockMode
                , block = Just (Block Earth UpLeft)
                }
              )
            , ( Undo
              , [ "///"
                , "   "
                ]
              , { mode = PlaceBlockMode
                , block = Just (Block Earth UpLeft)
                }
              )
            , ( (ChangeBlock NoBlock)
              , [ "///"
                , "   "
                ]
              , { mode = PlaceBlockMode
                , block = Just NoBlock
                }
              )
            , ( Redo
              , [ "///"
                , "  #"
                ]
              , { mode = PlaceBlockMode
                , block = Just NoBlock
                }
              )
            ]

        , testActions "Redoing when no history does nothing"
            (
                [ "MMM"
                , "MMM"
                ]
              , { mode = InitialMode, block = Nothing }
            )
            [ ( Redo
              , [ "MMM"
                , "MMM"
                ]
              , { mode = InitialMode, block = Nothing }
              )
            ]

        , testActions "Redoing past the end does nothing"
            (
                [ "MMM"
                , "MMM"
                ]
              , { mode = InitialMode, block = Nothing }
            )
            [ ( (LevelClick 2 1)
              , [ "MMM"
                , "MM#"
                ]
              , { mode = InitialMode, block = Nothing }
              )
            , ( Undo
              , [ "MMM"
                , "MMM"
                ]
              , { mode = InitialMode, block = Nothing }
              )
            , ( Redo
              , [ "MMM"
                , "MM#"
                ]
              , { mode = InitialMode, block = Nothing }
              )
            , ( Redo
              , [ "MMM"
                , "MM#"
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
            ( msg
            , { world = parseFixed lines
              , uiState = state
              , t = t
              , past = []
              , future = []
              }
            )

        initModel : Model
        initModel =
            { world = parseFixed initWorld
            , uiState = initState
            , t = t
            , past = []
            , future = []
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
            { m | past = [], future = [] }

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
