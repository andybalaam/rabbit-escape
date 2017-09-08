module Msg exposing (Msg(..))


import Model exposing (UiMode)


type Msg =
      Resize Int Int
    | LevelClick Int Int
    | ChangeMode UiMode
