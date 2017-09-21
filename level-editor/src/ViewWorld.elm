module ViewWorld exposing (viewWorld)

import Html exposing (Html, button, div, img)
import Html.Attributes exposing (id, class, src, style)
import Html.Events exposing (onClick)


import Model exposing (Model)
import Msg exposing (Msg(..))
import Rabbit exposing (Direction(..), Rabbit)
import World exposing
    ( Block(..)
    , BlockMaterial(..)
    , BlockShape(..)
    , World
    , rabbitsAt
    )


rabbitImage : Rabbit -> Html Msg
rabbitImage rabbit =
    let
        lr =
            case rabbit.dir of
                Left  -> "left"
                Right -> "right"
    in
        img
            [ src ("images/rabbit_stand_" ++ lr ++ ".svg")
            , class "thing"
            ]
            []


blockImage : Block -> Int -> Int -> List (Html Msg)
blockImage block x y =
    case block of
        Block Earth Flat ->
            [ img
                [ src "images/land_block_1.png" ]
                []
            ]
        _ ->
            []


viewBlockContents : Block -> List Rabbit -> Int -> Int -> Html Msg
viewBlockContents block rabbits x y =
    let
        sx = toString (x + 1)
        sy = toString (y + 1)
    in
        button
            [ onClick (LevelClick x y)
            , style
                [ ("grid-row-start", sy)
                , ("grid-row-end",   sy)
                , ("grid-column-start", sx)
                , ("grid-column-end",   sx)
                ]
            , id (sx++","++sy)
            ]
            ( blockImage block x y ++ List.map rabbitImage rabbits )


viewBlock : World -> Int -> Int -> Block -> Html Msg
viewBlock world y x block =
    viewBlockContents block (rabbitsAt world x y) x y


viewRow : World -> Int -> List Block -> List (Html Msg)
viewRow world y blocks =
    List.indexedMap (viewBlock world y) blocks


viewWorld : World -> Html Msg
viewWorld world =
    div
        [ id "level" ]
        (List.concat
            (List.indexedMap
                (viewRow world)
                (World.blocks world)
            )
        )
