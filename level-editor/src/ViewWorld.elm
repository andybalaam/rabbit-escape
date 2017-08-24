module ViewWorld exposing (viewWorld)

import Html exposing
    ( Html
    , button
    , div
    , img
    , pre
    , programWithFlags
    , table
    , text
    , tr
    , td
    )
import Html.Attributes exposing (class, height, id, src, style, width)
import Html.Events exposing (onClick)


import Model exposing (Model)
import Msg exposing (Msg(..))
import Rabbit exposing (Direction(..), Rabbit)
import ToolbarDims exposing (ToolbarDims)
import ToolbarOrientation exposing (ToolbarOrientation(..))
import Units exposing (..)
import World exposing
    ( Block(..)
    , BlockMaterial(..)
    , BlockShape(..)
    , World
    , rabbitsAt
    )


extraMargin : Pixels
extraMargin =
    Pixels 10  -- toolbar 4 + 2 * (border=1px + padding=2px)


rabbitImage : Em -> Rabbit -> Html Msg
rabbitImage square_width rabbit =
    let
        (lr, adj) =
            case rabbit.dir of
                Left -> ("left", 0)
                Right -> ("right", 0)

        s =  "game-images/rabbit_stand_" ++ lr ++ ".svg"
    in
        img
            [ class "thingimg"
            , src s
            , style
                [ ("width", square_width |> em)
                ]
            ]
            []


blockImage : Em -> Block -> Int -> Int -> List (Html Msg)
blockImage square_width block x y =
    case block of
        Block Earth Flat ->
            [ img
                [ class "levelimg"
                , src "game-images/land_block_1.png"
                , style
                    [ ("width", square_width |> em)
                    ]
                ]
                []
            ]
        _ ->
            []


viewBlockContents :
    Em -> Block -> List Rabbit -> Int -> Int -> Html Msg
viewBlockContents square_width block rabbits x y =
    button
        [ class "levelbutton"
        , style
            [ ("width", square_width |> em)
            , ("height", square_width |> em)
            , ("left", square_width :*: x |> em)
            , ("top",  square_width :*: y |> em)
            ]
        , onClick (LevelClick x y)
        ]
        (
            blockImage square_width block x y
                ++ List.map (rabbitImage square_width) rabbits
        )


viewBlock : Em -> World -> Int -> Int -> Block -> Html Msg
viewBlock square_width world y x block =
    viewBlockContents square_width block (rabbitsAt world x y) x y


viewRow : Em -> World -> Int -> List Block -> List (Html Msg)
viewRow square_width world y blocks =
    List.indexedMap (viewBlock square_width world y) blocks


toolbarMargin : ToolbarDims -> List (String, String)
toolbarMargin tbdims =
    let
        th = tbdims.thickness .+. extraMargin |> px
        ex = extraMargin |> px
    in
        case tbdims.orientation of
            LeftToolbar -> [("margin-left", th), ("margin-top", ex)]
            TopToolbar  -> [("margin-left", ex), ("margin-top", th)]


viewWorld : {x| square_width : Em, toolbar : ToolbarDims } -> World -> Html Msg
viewWorld dims world =
    div
        [ id "level"
        , style (toolbarMargin dims.toolbar)
        ]
        (List.concat
            (List.indexedMap
                (viewRow dims.square_width world)
                (World.blocks world)
            )
        )
