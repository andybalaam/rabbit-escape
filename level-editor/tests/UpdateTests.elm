module UpdateTests exposing (all)


import Dict
import Test exposing (describe,test,Test)
import Expect


import MetaLines
import Model exposing (Model, UiMode(..), UiState)
import Msg exposing (Msg(..))
import ParseErr exposing (ParseErr(..))
import Rabbit exposing (Direction(..), Rabbit, makeRabbit)
import Thing exposing (Thing(..))
import Update exposing (update)

import World exposing
    ( Block(..)
    , BlockMaterial(..)
    , BlockShape(..)
    , World
    , makeBlockGrid
    , makeWorld
    )

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
            , emptyState
            )
            [ ( (LevelClick 2 1)
              , [ "####"
                , "# ##"
                , "# r#"
                , "####"
                ]
              , emptyState
              )
            ]

        , testActions "Clicking full replaces with chosen block"
            ( [ "####"
              , "#  #"
              , "# r#"
              , "####"
              ]
            , { emptyState
              | mode = PlaceBlockMode
              , block = Just (Block Earth UpRight)
              }
            )
            [ ( (LevelClick 0 1)
              , [ "####"
                , "/  #"
                , "# r#"
                , "####"
                ]
              , { emptyState
                | mode = PlaceBlockMode
                , block = Just (Block Earth UpRight)
                }
              )
            ]

        , testActions "Placing a rabbit"
            ( [ "####"
              , "#  #"
              , "#  #"
              , "####"
              ]
            , { emptyState
              | mode = PlaceRabbitMode
              , rabbit = Just (makeRabbit 0 0 Left)
              }
            )
            [ ( (LevelClick 2 1)
              , [ "####"
                , "# j#"
                , "#  #"
                , "####"
                ]
              , { emptyState
                | mode = PlaceRabbitMode
                , rabbit = Just (makeRabbit 0 0 Left)
                }
              )
            ]


        , testActions "Placing a thing"
            ( [ "####"
              , "#  #"
              , "#  #"
              , "####"
              ]
            , { emptyState
              | mode = PlaceThingMode
              , thing = Just (Just (Entrance 0 0))
              }
            )
            [ ( (LevelClick 1 2)
              , [ "####"
                , "#  #"
                , "#Q #"
                , "####"
                ]
              , { emptyState
                | mode = PlaceThingMode
                , thing = Just (Just (Entrance 0 0))
                }
              )
            ]


        , testActions "Removing a thing"
            ( [ "####"
              , "# O#"
              , "#  #"
              , "####"
              ]
            , { emptyState
              | mode = PlaceThingMode
              , thing = Just Nothing
              }
            )
            [ ( (LevelClick 2 1)
              , [ "####"
                , "#  #"
                , "#  #"
                , "####"
                ]
              , { emptyState
                | mode = PlaceThingMode
                , thing = Just Nothing
                }
              )
            ]


        , testActions "Removing a rabbit"
            ( [ "####"
              , "# j#"
              , "# r#"
              , "####"
              ]
            , { emptyState
              | mode = PlaceRabbitMode
              , rabbit = Nothing
              }
            )
            [ ( (LevelClick 1 1)  -- Click on nothing
              , [ "####"
                , "# j#"
                , "# r#"
                , "####"
                ]
              , { emptyState
                | mode = PlaceRabbitMode
                , rabbit = Nothing
                }
              )
            , ( (LevelClick 2 2)  -- Click the rabbit
              , [ "####"
                , "# j#"
                , "#  #"
                , "####"
                ]
              , { emptyState
                | mode = PlaceRabbitMode
                , rabbit = Nothing
                }
              )
            ]

        , testActions "ChangeMode changes mode"
            ( [ "" ]
            , emptyState
            )
            [ ( (ChangeMode ChooseBlockMode)
              , [ "" ]
              , { emptyState | mode = ChooseBlockMode }
              )
            , ( (ChangeMode ChooseRabbitMode)
              , [ "" ]
              , { emptyState | mode = ChooseRabbitMode }
              )
            ]

        , testActions "Changing to code mode stores code text"
            ( [ "#r#" ]
            , emptyState
            )
            [ ( (ChangeMode (CodeMode ""))  -- Even though we sent nothing
              , [ "#r#" ]
              , { emptyState | mode = CodeMode "#r#" }
                -- We get a model
              )
            ]

        , testActions "Changing to code mode deletes cached world text"
            ( [ "#r#" ]
            , { emptyState
              | newWorld =
                Just ("foo" , Err (UnrecognisedChar {row=1, col=1} 'f'))
              }
            )
            [ ( (ChangeMode (CodeMode ""))
              , [ "#r#" ]
              , { emptyState
                | mode = CodeMode "#r#"
                , newWorld = Nothing -- "foo" etc was removed
                }
              )
            ]

        , testActions "Can update code"
            ( [ "#r#" ]
            , { emptyState
              | newWorld =
                Just ("#" , parse "" "#")
              }
            )
            [ ( ChangeCode
              , [ "#" ]
              , { emptyState
                | mode = InitialMode
                }
              )
            ]

        , testActions "Choosing a block updates block and mode"
            ( [ "" ]
            , emptyState
            )
            [ ( (ChangeBlock (Block Earth UpRight))
              , [ "" ]
              , { emptyState
                | mode = PlaceBlockMode
                , block = Just (Block Earth UpRight)
                }
              )
            ]

        , testActions "Choosing a rabbit updates rabbit and mode"
            ( [ "" ]
            , emptyState
            )
            [ ( (ChangeRabbit (Just (makeRabbit 0 0 Left)))
              , [ "" ]
              , { emptyState
                | mode = PlaceRabbitMode
                , rabbit = Just (makeRabbit 0 0 Left)
                }
              )
            ]

        , testActions "Can undo an action"
            (
                [ "   "
                , "   "
                ]
              , emptyState
            )
            [ ( (LevelClick 2 0)
              , [ "  #"
                , "   "
                ]
              , emptyState
              )
            , ( Undo
              , [ "   "
                , "   "
                ]
              , emptyState
              )
            ]

        , testActions "Undo skips actions that don't modify the world"
            (
                [ "   "
                , "   "
                ]
              , emptyState
            )
            [ ( (LevelClick 2 0)
              , [ "  #"
                , "   "
                ]
              , emptyState
              )
            , ( (ChangeBlock (Block Earth UpRight))
              , [ "  #"
                , "   "
                ]
              , { emptyState
                | mode = PlaceBlockMode
                , block = Just (Block Earth UpRight)
                }
              )
            , ( Undo
              , [ "   "  -- The world goes back to before
                , "   "  -- but the mode and block stay selected.
                ]
              , { emptyState
                | mode = PlaceBlockMode
                , block = Just (Block Earth UpRight)
                }
              )
            ]

        , testActions "Can undo multiple actions"
            (
                [ "/  "
                , "/  "
                ]
              , emptyState
            )
            [ ( (LevelClick 2 0)
              , [ "/ #"
                , "/  "
                ]
              , emptyState
              )
            , ( (LevelClick 1 1)
              , [ "/ #"
                , "/# "
                ]
              , emptyState
              )
            , ( (LevelClick 2 1)
              , [ "/ #"
                , "/##"
                ]
              , emptyState
              )
            , ( Undo
              , [ "/ #"
                , "/# "
                ]
              , emptyState
              )
            , ( Undo
              , [ "/ #"
                , "/  "
                ]
              , emptyState
              )
            , ( Undo
              , [ "/  "
                , "/  "
                ]
              , emptyState
              )
            ]

        , testActions "Undoing when no history does nothing"
            (
                [ "MMM"
                , "MMM"
                ]
              , emptyState
            )
            [ ( Undo
              , [ "MMM"
                , "MMM"
                ]
              , emptyState
              )
            ]

        , testActions "Undoing back past the beginning does nothing"
            (
                [ "MMM"
                , "MMM"
                ]
              , emptyState
            )
            [ ( (LevelClick 2 1)
              , [ "MMM"
                , "MM#"
                ]
              , emptyState
              )
            , ( Undo
              , [ "MMM"
                , "MMM"
                ]
              , emptyState
              )
            , ( Undo
              , [ "MMM"
                , "MMM"
                ]
              , emptyState
              )
            ]

        , testActions "Redo restores an undone action"
            (
                [ "MMM"
                , "MM/"
                ]
              , emptyState
            )
            [ ( (LevelClick 2 1)
              , [ "MMM"
                , "MM#"
                ]
              , emptyState
              )
            , ( Undo
              , [ "MMM"
                , "MM/"
                ]
              , emptyState
              )
            , ( Redo
              , [ "MMM"
                , "MM#"
                ]
              , emptyState
              )
            ]

        , testActions "Doing something new removes redo stack"
            (
                [ "MMM"
                , "MM/"
                ]
              , emptyState
            )
            [ ( (LevelClick 2 1)
              , [ "MMM"
                , "MM#"
                ]
              , emptyState
              )
            , ( Undo
              , [ "MMM"
                , "MM/"
                ]
              , emptyState
              )
            , ( (LevelClick 0 0)
              , [ "#MM"
                , "MM/"
                ]
              , emptyState
              )
            , ( Redo
              , [ "#MM"  -- The redo did nothing because
                , "MM/"  -- we had placed a block in between
                ]
              , emptyState
              )
            , ( Undo
              , [ "MMM"  -- Undo still works
                , "MM/"
                ]
              , emptyState
              )
            , ( Redo
              , [ "#MM"  -- And redo undoes the undo
                , "MM/"
                ]
              , emptyState
              )
            ]

        , testActions "Non-modifying actions don't affect redo"
            (
                [ "///"
                , "   "
                ]
              , emptyState
            )
            [ ( (LevelClick 2 1)
              , [ "///"
                , "  #"
                ]
              , emptyState
              )
            , ( Undo
              , [ "///"
                , "   "
                ]
              , emptyState
              )
            , ( (ChangeBlock (Block Earth UpRight))
              , [ "///"
                , "   "
                ]
              , { emptyState
                | mode = PlaceBlockMode
                , block = Just (Block Earth UpRight)
                }
              )
            , ( Redo
              , [ "///"
                , "  #"
                ]
              , { emptyState
                | mode = PlaceBlockMode
                , block = Just (Block Earth UpRight)
                }
              )
            , ( (ChangeBlock (Block Earth UpLeft))
              , [ "///"
                , "  #"
                ]
              , { emptyState
                | mode = PlaceBlockMode
                , block = Just (Block Earth UpLeft)
                }
              )
            , ( Undo
              , [ "///"
                , "   "
                ]
              , { emptyState
                | mode = PlaceBlockMode
                , block = Just (Block Earth UpLeft)
                }
              )
            , ( (ChangeBlock NoBlock)
              , [ "///"
                , "   "
                ]
              , { emptyState
                | mode = PlaceBlockMode
                , block = Just NoBlock
                }
              )
            , ( Redo
              , [ "///"
                , "  #"
                ]
              , { emptyState
                | mode = PlaceBlockMode
                , block = Just NoBlock
                }
              )
            ]

        , testActions "Redoing when no history does nothing"
            (
                [ "MMM"
                , "MMM"
                ]
              , emptyState
            )
            [ ( Redo
              , [ "MMM"
                , "MMM"
                ]
              , emptyState
              )
            ]

        , testActions "Redoing past the end does nothing"
            (
                [ "MMM"
                , "MMM"
                ]
              , emptyState
            )
            [ ( (LevelClick 2 1)
              , [ "MMM"
                , "MM#"
                ]
              , emptyState
              )
            , ( Undo
              , [ "MMM"
                , "MMM"
                ]
              , emptyState
              )
            , ( Redo
              , [ "MMM"
                , "MM#"
                ]
              , emptyState
              )
            , ( Redo
              , [ "MMM"
                , "MM#"
                ]
              , emptyState
              )
            ]

        , testActions "Adding a column"
            ( [ "####"
              , "#  #"
              , "# r#"
              , "####"
              ]
            , emptyState
            )
            [ ( AddColumn
              , [ "#### "
                , "#  # "
                , "# r# "
                , "#### "
                ]
              , emptyState
              )
            ]

        , testActions "Removing a column"
            ( [ "####"
              , "# jr"
              , "#  O"
              , "####"
              ]
            , emptyState
            )
            [ ( RemoveColumn
              , [ "###"
                , "# j"
                , "#  "
                , "###"
                ]
              , emptyState
              )
            ]

        , testActions "Adding a row"
            ( [ "####"
              , "#  #"
              , "#j #"
              , "####"
              ]
            , emptyState
            )
            [ ( AddRow
              , [ "####"
                , "#  #"
                , "#j #"
                , "####"
                , "    "
                ]
              , emptyState
              )
            ]

        , testActions "Removing a row"
            ( [ "####"
              , "#  #"
              , "#j #"
              , "Q#r#"
              ]
            , emptyState
            )
            [ ( RemoveRow
              , [ "####"
                , "#  #"
                , "#j #"
                ]
              , emptyState
              )
            ]

        ]


parseFixed : List String -> World
parseFixed textLines =
    case parse "" (String.join "\n" textLines) of
        Err e ->
            makeWorld
                "Parsing failed!"
                (makeBlockGrid [])
                []
                []
                MetaLines.defaults
        Ok w ->
            w


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
                        ( "Step " ++ (String.fromInt num) )
                        ( expectEqualWithoutHistory
                            ( expectedModel, Cmd.none )
                            ( newModel, newCmd )
                        )
            in
                headTest :: expectUpdateGives (num + 1) newModel mms


emptyState : UiState
emptyState =
    { mode = InitialMode
    , block = Nothing
    , rabbit = Nothing
    , thing = Nothing
    , newMetaLines = MetaLines.emptyDiff
    , newWorld = Nothing
    }
