import Browser
import Html exposing (Html)




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
                    MetaLines.defaults


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


type alias Flags = String

init : Flags -> ( Model, Cmd msg )
init flags =
    (initModel, Cmd.none)


main =
  Browser.element
    { init = init
    , update = update
    , view = view
    , subscriptions = \(model) -> Sub.none
    }
