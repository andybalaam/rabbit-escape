module Model exposing (Model, ModelScreen)


import Units exposing (..)
import World exposing (World)


type alias ModelScreen =
    { width : Pixels
    , height : Pixels
    }


type alias Model =
    { screen : ModelScreen
    , world : World
    --, past : List World
    --, future: List World
    }
