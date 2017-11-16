module Msg exposing (Msg(..))


import Model exposing (UiMode)
import Rabbit exposing (Rabbit)
import Thing exposing (Thing)
import World exposing (Block)


type Msg =
      LevelClick Int Int
    | ChangeMode UiMode
    | ChangeBlock Block
    | ChangeThing (Maybe Thing)
    | ChangeRabbit (Maybe Rabbit)
    | AddColumn
    | RemoveColumn
    | AddRow
    | RemoveRow
    | Undo
    | Redo
    | DetailsInput String String
    | ChangeDetails
    | CodeInput String
    | ChangeCode
