import Html exposing
    ( Html
    , div
    , img
    , pre
    , programWithFlags
    , table
    , text
    , tr
    , td
    )
import Html.Attributes exposing (height, src, style, width)
import Window


import Rabbit exposing (Direction(..), Rabbit)
import World exposing
    ( Block(..)
    , BlockMaterial(..)
    , BlockShape(..)
    , World
    , makeBlockGrid
    , makeWorld
    , rabbitsAt
    )
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
                "Example level"
                (  "######\n"
                ++ "#r   #\n"
                ++ "#  j #\n"
                ++ "######\n"
                )
    in
        case p of
            Ok w -> w
            Err s -> makeWorld "Unexpected Error" (makeBlockGrid []) []


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


rabbitImage : Int -> Rabbit -> Html Msg
rabbitImage sq_width rabbit =
    let
        (lr, adj) =
            case rabbit.dir of
                Left -> ("left", -sq_width)
                Right -> ("right", 0)

        s =  "game-images/rabbit_walk_" ++ lr ++ "_01.svg"
    in
        img
            [ src s
            , style
                [ ("position", "relative")
                , ("width", (toString (sq_width * 2)) ++ "px")
                , ("margin-right", (toString -sq_width) ++ "px")
                , ("left", (toString adj) ++ "px")
                ]
            ]
            []


blockImage : Int -> Block -> Int -> Int -> List (Html Msg)
blockImage sq_width block x y =
    case block of
        Block Earth Flat -> [img [src "game-images/land_block_1.png", width sq_width][]]
        _ -> []


viewBlockContents : Int -> Block -> List Rabbit -> Int -> Int -> List (Html Msg)
viewBlockContents sq_width block rabbits x y =
    blockImage sq_width block x y ++ List.map (rabbitImage sq_width) rabbits


viewBlock : Int -> World -> Int -> Int -> Block -> Html Msg
viewBlock sq_width world y x block =
    td
        [ style
            [ ("width",  (toString sq_width) ++ "px")
            , ("height", (toString sq_width) ++ "px")
            ]
        ]
        (viewBlockContents sq_width block (rabbitsAt world x y) x y)


viewRow : Int -> World -> Int -> List Block -> Html Msg
viewRow sq_width world y blocks =
    tr
        [ style
            [ ("height", (toString sq_width) ++ "px")
            ]
        ]
        (List.indexedMap (viewBlock sq_width world y) blocks)


view : Model -> Html Msg
view model =
    let
        s = model.screen
        w = model.world
        sq_width = 20
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
                ,
                table
                    []
                    (List.indexedMap (viewRow sq_width w) (World.blocks w))

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
