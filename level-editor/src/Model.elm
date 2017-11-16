module Model exposing (Model, UiMode(..), UiState)


import MetaLines
import ParseErr exposing (ParseErr(..))
import Rabbit exposing (Rabbit)
import Thing exposing (Thing)
import World exposing (Block(..), World)


type UiMode =
      InitialMode
    | CodeMode String
    | ChooseBlockMode
    | PlaceBlockMode
    | ChooseThingMode
    | PlaceThingMode
    | ChooseRabbitMode
    | PlaceRabbitMode
    | ModifyDetailsMode


type alias UiState =
    { mode : UiMode
    , block : Maybe Block
    , rabbit : Maybe Rabbit
    , thing : Maybe (Maybe Thing)
    , newMetaLines : MetaLines.Diff
    , newWorld : Maybe (String, Result ParseErr World)
    }


type alias Model =
    { world : World
    , uiState : UiState
    , t : String -> String
    , past : List World
    , future: List World
    }
