module Model exposing (Model, ModelScreen)


import World exposing (World)


type alias ModelScreen =
    { width : Int
    , height : Int
    }


type alias Model =
    { screen : ModelScreen
    , world : World
    --, past : List World
    --, future: List World
    }
