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
import Html.Attributes exposing (height, src, style, width)


import Model exposing (Model)
import Msg exposing (Msg)
import Rabbit exposing (Direction(..), Rabbit)
import World exposing
    ( Block(..)
    , BlockMaterial(..)
    , BlockShape(..)
    , World
    , rabbitsAt
    )


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


viewWorld : World -> Int -> Html Msg
viewWorld world sq_width =
    table
        []
        (List.indexedMap (viewRow sq_width world) (World.blocks world))
