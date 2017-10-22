module ViewWorld exposing (viewWorld)

import Html exposing (Html, button, div, img)
import Html.Attributes exposing (id, class, src, style)
import Html.Events exposing (onClick)


import BlockImage exposing (blockImage)
import Model exposing (Model)
import Msg exposing (Msg(..))
import Rabbit exposing (Direction(..), Rabbit)
import RabbitImage exposing (rabbitImage)
import World exposing
    ( Block(..)
    , BlockMaterial(..)
    , BlockShape(..)
    , World
    , rabbitsAt
    )


rabbitImg : Rabbit -> Html Msg
rabbitImg rabbit =
    img
        [ src ("images/" ++ rabbitImage (Just rabbit))
        , class "thing"
        ]
        []


blockImg : Block -> Int -> Int -> List (Html Msg)
blockImg block x y =
    case block of
        NoBlock -> []
        default ->
            [ img
                [ src ("images/" ++ blockImage block) ]
                []
            ]


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
            ( blockImg block x y ++ List.map rabbitImg rabbits )


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
