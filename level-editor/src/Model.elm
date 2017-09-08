module Model exposing (Model, ModelScreen, UiMode(..))


import Units exposing (..)
import World exposing (World)


type UiMode =
      InitialMode
    | ChooseBlockMode


type alias ModelScreen =
    { width : Pixels
    , height : Pixels
    }


type alias Model =
    { screen : ModelScreen
    , world : World
    , uiMode : UiMode
    --, past : List World
    --, future: List World
    }
