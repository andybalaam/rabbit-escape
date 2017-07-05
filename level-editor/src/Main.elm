import Html exposing (Html, programWithFlags, text)
import Window


import World exposing(World, initWorld)


type alias Model =
    { screen :
        { width : Int
        , height : Int
        }
    , world : World
    , past : List World
    , future: List World
    }


type alias Flags =
    { width : Int
    , height : Int
    }


type Msg =
      Resize Int Int


initModel : Flags -> Model
initModel flags =
    { screen =
        { width = flags.width
        , height = flags.height
        }
    , world = initWorld
    , past = []
    , future = []
    }


init : Flags -> (Model, Cmd Msg)
init flags =
    (initModel flags, Cmd.none)


view : Model -> Html Msg
view model =
    let
        s = model.screen
    in
        text
            (  (toString s.width)
            ++ ", "
            ++ (toString s.height)
            )


update : Msg -> Model -> (Model, Cmd Msg)
update msg model =
    let
        m =
            case msg of
                Resize w h -> {model|screen = {width=w, height=h}}
    in
        (m, Cmd.none)


subscriptions : Model -> Sub Msg
subscriptions model =
    Sub.batch
        [ Window.resizes(\size -> Resize size.width size.height)
        ]


main =
   programWithFlags
     { init = init
     , view = view
     , update = update
     , subscriptions = subscriptions
     }
