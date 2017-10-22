module Msg exposing (Msg(..))


import Model exposing (UiMode)
import Rabbit exposing (Rabbit)
import World exposing (Block)


type Msg =
      LevelClick Int Int
    | ChangeMode UiMode
    | ChangeBlock Block
    | ChangeRabbit (Maybe Rabbit)
    | Undo
    | Redo
