module Msg exposing (Msg(..))


import Model exposing (UiMode)


type Msg =
      LevelClick Int Int
    | ChangeMode UiMode
