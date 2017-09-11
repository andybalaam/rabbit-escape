module Model exposing (Model, ModelScreen, UiMode(..), UiState)


import Units exposing (..)
import World exposing (Block(..), World)


type UiMode =
      InitialMode
    | ChooseBlockMode


type alias UiState =
    { mode : UiMode
    , block : Block
    }


type alias ModelScreen =
    { width : Pixels
    , height : Pixels
    }


type alias Model =
    { screen : ModelScreen
    , world : World
    , uiState : UiState
    --, past : List World
    --, future: List World
    }
