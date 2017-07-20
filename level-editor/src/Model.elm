module Model exposing (Model)


import World exposing (World)


type alias Model =
    { screen :
        { width : Int
        , height : Int
        }
    , world : World
    --, past : List World
    --, future: List World
    }
