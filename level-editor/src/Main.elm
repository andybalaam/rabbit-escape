import Html exposing (Html, div, pre, programWithFlags, text)
import Window


import World exposing (World, makeBlockGrid, makeWorld)
import WorldParser exposing (parse)
import WorldTextRender exposing (render)


type alias Model =
    { screen :
        { width : Int
        , height : Int
        }
    , world : World
    --, past : List World
    --, future: List World
    }


type alias Flags =
    { width : Int
    , height : Int
    }


type Msg =
      Resize Int Int


initWorld : World
initWorld =
    let
        p =
            WorldParser.parse
                "Empty level"
                (  "####\n"
                ++ "#  #\n"
                ++ "#  #\n"
                ++ "####\n"
                )
    in
        case p of
            Ok w -> w
            Err s -> makeWorld "Unexpected Error" (makeBlockGrid [])


initModel : Flags -> Model
initModel flags =
    { screen =
        { width = flags.width
        , height = flags.height
        }
    , world = initWorld
    --, past = []
    --, future = []
    }


init : Flags -> (Model, Cmd Msg)
init flags =
    (initModel flags, Cmd.none)


view : Model -> Html Msg
view model =
    let
        s = model.screen
    in
        div
            []
            [
                text
                    (  (toString s.width)
                    ++ ", "
                    ++ (toString s.height)
                    )
                ,
                pre
                    []
                    [ text (render model.world)
                    ]
            ]


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
