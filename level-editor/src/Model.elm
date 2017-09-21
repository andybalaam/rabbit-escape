module Model exposing (Model, UiMode(..), UiState)


import World exposing (Block(..), World)


type UiMode =
      InitialMode
    | ChooseBlockMode


type alias UiState =
    { mode : UiMode
    , block : Block
    }


type alias Model =
    { world : World
    , uiState : UiState
    --, past : List World
    --, future: List World
    }
