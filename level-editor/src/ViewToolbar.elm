module ViewToolbar exposing (viewToolbar)

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


import Msg exposing (Msg)
import Rabbit exposing (Direction(..))
import ToolbarDims exposing (ToolbarDims)
import ToolbarOrientation exposing (ToolbarOrientation(..))
import Units exposing (..)
import World exposing (BlockMaterial(..), BlockShape(..))


type ButtonDef =
      SaveButton String
    | BlockButton BlockMaterial BlockShape String
    | RabbitButton Direction String


margin : Pixels
margin =
    Pixels 2


buttonsList : List ButtonDef
buttonsList =
    [ SaveButton "save.svg"
    , BlockButton Earth Flat "land_block_1.png"
    , RabbitButton Left "rabbit_stand_left.svg"
    ]


styles : ToolbarOrientation -> List (Html.Attribute Msg)
styles orientation =
    case orientation of
        LeftToolbar ->
            [
                style
                    [ ("width", "6em")
                    , ("height", "30em")
                    , ("overflow-x", "hidden")
                    , ("overflow-y", "auto")
                    ]
            ]
        TopToolbar ->
            [
                style
                    [ ("width", "30em")
                    , ("height", "6em")
                    , ("overflow-x" ,"auto")
                    , ("overflow-y", "hidden")
                    , ("white-space", "nowrap")
                    ]
            ]


viewButton : ToolbarDims -> ButtonDef -> Html Msg
viewButton tbdims def =
    let imgfile =
        case def of
            SaveButton s -> s
            BlockButton _ _ s -> s
            RabbitButton _ s -> s
    in
        button
            [ class "button"
            , style
                [ ("width", px (tbdims.thickness `px_minus` margin))
                , ("height", "6em")
                ]
            ]
            [ img
                [ class "buttonimg"
                , src ("game-images/" ++ imgfile)
                , style
                    [ ("width", "4.5em")
                    , ("height", "4.5em")
                    ]
                ]
                []
            ]

buttons : ToolbarDims -> List (Html Msg)
buttons tbdims =
    List.map (viewButton tbdims) buttonsList


viewToolbar : {x| toolbar : ToolbarDims } -> Html Msg
viewToolbar dims =
    div
        ([ id "toolbar" ] ++ styles dims.toolbar.orientation)
        (buttons dims.toolbar)
