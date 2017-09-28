module Model exposing (Model, UiMode(..), UiState)


import World exposing (Block(..), World)


type UiMode =
      InitialMode
    | CodeMode String
    | ChooseBlockMode
    | PlaceBlockMode


type alias UiState =
    { mode : UiMode
    , block : Maybe Block
    }


type alias Model =
    { world : World
    , uiState : UiState
    --, past : List World
    --, future: List World
    }
