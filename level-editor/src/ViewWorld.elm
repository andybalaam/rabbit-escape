module ViewWorld exposing (viewWorld)

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
import Html.Attributes exposing (class, height, src, style, width)


import Model exposing (Model)
import Msg exposing (Msg)
import Rabbit exposing (Direction(..), Rabbit)
import Units exposing (..)
import World exposing
    ( Block(..)
    , BlockMaterial(..)
    , BlockShape(..)
    , World
    , rabbitsAt
    )


rabbitImage : Float -> Rabbit -> Html Msg
rabbitImage blockWidth rabbit =
    let
        (lr, adj) =
            case rabbit.dir of
                Left -> ("left", 0)
                Right -> ("right", 0)

        s =  "game-images/rabbit_walk_" ++ lr ++ "_01.svg"
    in
        img
            [ src s
            , style
                [ ("position", "relative")
                , ("width", pc blockWidth)
                , ("margin-right", (toString 0) ++ "px")
                , ("left", (toString adj) ++ "px")
                ]
            ]
            []


pc : Float -> String
pc f =
    (toString f) ++ "em"


blockImage : Float -> Block -> Int -> Int -> List (Html Msg)
blockImage blockWidth block x y =
    case block of
        Block Earth Flat ->
            [ img
                [ src "game-images/land_block_1.png"
                , style
                    [ ("width", pc blockWidth)
                    , ("left", pc (blockWidth * (toFloat x)))
                    , ("top",  pc (blockWidth * (toFloat y)))
                    ]
                ]
                []
            ]
        _ ->
            []


viewBlockContents :
    Float -> Block -> List Rabbit -> Int -> Int -> List (Html Msg)
viewBlockContents blockWidth block rabbits x y =
    blockImage blockWidth block x y ++ List.map (rabbitImage blockWidth) rabbits


viewBlock : Float -> World -> Int -> Int -> Block -> List (Html Msg)
viewBlock blockWidth world y x block =
    viewBlockContents blockWidth block (rabbitsAt world x y) x y


viewRow : Float -> World -> Int -> List Block -> List (Html Msg)
viewRow blockWidth world y blocks =
    List.concat (List.indexedMap (viewBlock blockWidth world y) blocks)


viewWorld : {x| square_width : Em } -> World -> Html Msg
viewWorld dims world =
    let
        blockWidth = 100.0 / toFloat (World.width world)
    in
        div
            [ class "world"
            ]
            (List.concat
                (List.indexedMap
                    (viewRow blockWidth world)
                    (World.blocks world)
                )
            )
