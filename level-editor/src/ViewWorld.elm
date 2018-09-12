module ViewWorld exposing (viewWorld)

import Html exposing (Html, button, div, img)
import Html.Attributes exposing (id, class, src, style)
import Html.Events exposing (onClick)


import BlockImage exposing (blockImage)
import Model exposing (Model)
import Msg exposing (Msg(..))
import Rabbit exposing (Direction(..), Rabbit)
import RabbitImage exposing (rabbitImage)
import Thing exposing (Thing)
import ThingImage exposing (thingImage)
import World exposing
    ( Block(..)
    , BlockMaterial(..)
    , BlockShape(..)
    , World
    , rabbitsAt
    , thingsAt
    )


rabbitImg : Rabbit -> Html Msg
rabbitImg rabbit =
    img
        [ src ("images/" ++ rabbitImage (Just rabbit))
        , class "thing"
        ]
        []


thingImg : Thing -> Html Msg
thingImg thing =
    img
        [ src ("images/" ++ thingImage (Just thing))
        , class "thing"
        ]
        []


blockImg : Block -> Int -> Int -> List (Html Msg)
blockImg block x y =
    case block of
        NoBlock -> []
        _ ->
            [ img
                [ src ("images/" ++ blockImage block) ]
                []
            ]


buttonAttrs : Int -> Int -> List (Html.Attribute Msg)
buttonAttrs x y =
    let
        sx = String.fromInt (x + 1)
        sy = String.fromInt (y + 1)
    in
        [ style "grid-row-start" sy
        , style "grid-row-end" sy
        , style "grid-column-start" sx
        , style "grid-column-end" sx
        , id (sx ++ "," ++ sy)
        ]



viewBlockContents :
    Block ->
    List Rabbit ->
    List Thing ->
    Int ->
    Int ->
    Html Msg
viewBlockContents block rabbits things x y =
        button
            ( [ onClick (LevelClick x y) ] ++ buttonAttrs x y )
            (  blockImg block x y
            ++ List.map thingImg things
            ++ List.map rabbitImg rabbits
            )


addCol : Int -> Int -> List (Html Msg)
addCol x y =
    case y of
        0 ->
            [ button
                ( [ onClick (AddColumn) ] ++ buttonAttrs x y )
                [ img [ src "images/add_column.svg" ] [] ]
            ]
        1 ->
            [ button
                ( [ onClick (RemoveColumn) ] ++ buttonAttrs x y )
                [ img [ src "images/remove_column.svg" ] [] ]
            ]
        _ ->
            []


addRow : Int -> List (Html Msg)
addRow y =
    [ button
        ( [ onClick (AddRow) ] ++ buttonAttrs 0 y )
        [ img [ src "images/add_row.svg" ] [] ]
    , button
        ( [ onClick (RemoveRow) ] ++ buttonAttrs 1 y )
        [ img [ src "images/remove_row.svg" ] [] ]
    ]


viewBlock : World -> Int -> Int -> Block -> Html Msg
viewBlock world y x block =
    viewBlockContents block (rabbitsAt world x y) (thingsAt world x y) x y


viewRow : World -> Int -> List Block -> List (Html Msg)
viewRow world y blocks =
       ( List.indexedMap (viewBlock world y) blocks)
    ++ addCol (List.length blocks) y


viewWorld : World -> Html Msg
viewWorld world =
    div
        [ id "level" ]
        (
            ( List.concat
                (List.indexedMap
                    (viewRow world)
                    (World.blocks world)
                )
            )
        ++ addRow (List.length (World.blocks world))
        )
