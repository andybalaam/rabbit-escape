import Html exposing (Html, program)


import Window


import MetaLines
import Model exposing (Model, UiMode(..), UiState)
import Msg exposing (Msg(..))
import Rabbit exposing (Direction(..), Rabbit, makeRabbit)
import Update exposing (update)
import View exposing (view)
import World exposing
    ( Block(..)
    , BlockMaterial(..)
    , BlockShape(..)
    , World
    , makeBlockGrid
    , makeWorld
    )
import WorldParser exposing (parse)
import WorldTextRender exposing (render)


initWorld : World
initWorld =
    let
        p =
            WorldParser.parse
                "Example level"
                (  "######\n"
                ++ "#r   #\n"
                ++ "#  j #\n"
                ++ "######\n"
                )
    in
        case p of
            Ok w ->
                w
            Err s ->
                makeWorld
                    "Unexpected Error"
                    (makeBlockGrid [])
                    []
                    []
                    MetaLines.default


translationPlaceholder : String -> String
translationPlaceholder x =
    x


initModel : Model
initModel =
    { world = initWorld
    , uiState =
        { mode = InitialMode
        , block = Nothing
        , rabbit = Just (makeRabbit 0 0 Right)
        , thing = Nothing
        , newMetaLines = MetaLines.emptyDiff
        , newWorld = Nothing
        }
    , t = translationPlaceholder
    , past = []
    , future = []
    }


init : (Model, Cmd Msg)
init =
    (initModel, Cmd.none)


main =
   program
     { init = init
     , view = view
     , update = update
     , subscriptions = \(model) -> Sub.none
     }
