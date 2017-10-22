module Model exposing (Model, UiMode(..), UiState)


import Rabbit exposing (Rabbit)

import World exposing (Block(..), World)


type UiMode =
      InitialMode
    | CodeMode String
    | ChooseBlockMode
    | PlaceBlockMode
    | ChooseRabbitMode
    | PlaceRabbitMode


type alias UiState =
    { mode : UiMode
    , block : Maybe Block
    , rabbit : Maybe Rabbit
    }


type alias Model =
    { world : World
    , uiState : UiState
    , t : String -> String
    , past : List World
    , future: List World
    }
