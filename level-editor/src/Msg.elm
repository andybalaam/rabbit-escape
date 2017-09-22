module Msg exposing (Msg(..))


import Model exposing (UiMode)
import World exposing (Block(..))


type Msg =
      LevelClick Int Int
    | ChangeMode UiMode
    | ChangeBlock Block
