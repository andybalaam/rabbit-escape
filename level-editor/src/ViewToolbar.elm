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
import Html.Events exposing (onClick)


import Msg exposing (Msg(..))
import Model exposing (UiMode(..), UiState)
import Rabbit exposing (Direction(..))
import ToolbarDims exposing (ToolbarDims)
import ToolbarOrientation exposing (ToolbarOrientation(..))
import Units exposing (..)
import World exposing (BlockMaterial(..), BlockShape(..))


type ButtonDef =
      SaveButton
    | BlockButton
    | RabbitButton


margin : Pixels
margin =
    Pixels 2


buttonsList : List ButtonDef
buttonsList =
    [ SaveButton
    , BlockButton
    , RabbitButton
    ]


styles : ToolbarDims -> List (Html.Attribute Msg)
styles tbdims =
    let
        th = tbdims.thickness |> px
        len = tbdims.screenLength |> px
    in
        case tbdims.orientation of
            LeftToolbar ->
                [
                    style
                        [ ("width", th)
                        , ("height", len)
                        , ("overflow-x", "hidden")
                        , ("overflow-y", "auto")
                        ]
                ]
            TopToolbar ->
                [
                    style
                        [ ("width", len)
                        , ("height", th)
                        , ("overflow-x" ,"auto")
                        , ("overflow-y", "hidden")
                        , ("white-space", "nowrap")
                        ]
                ]


buildClickCmd : UiState -> ButtonDef -> Msg
buildClickCmd uiState buttonDef =
    case buttonDef of
        BlockButton -> ChangeMode ChooseBlockMode
        default     -> ChangeMode InitialMode


buttonImage : UiState -> ButtonDef -> String
buttonImage uiState buttondef =
    case buttondef of
        SaveButton -> "save.svg"
        BlockButton -> "land_block_1.png"
        RabbitButton -> "rabbit_stand_right.svg"


buttonClass : UiMode -> ButtonDef -> String
buttonClass mode buttondef =
    let
        pressedTypes =
            case mode of
                InitialMode -> []
                ChooseBlockMode -> [BlockButton]
    in
        if List.member buttondef pressedTypes then
            " pressed"
        else
            ""

viewButton : UiState -> ToolbarDims -> ButtonDef -> Html Msg
viewButton uiState tbdims def =
    let
        clickCmd = buildClickCmd uiState def
        imgclass = buttonClass uiState.mode def
        imgfile = buttonImage uiState def
        marg = margin |> px
        size = (tbdims.thickness .-. (margin .*. 2)) |> px
        img_size = ((tbdims.thickness .**. 0.8) .-. (margin .*. 2)) |> px
    in
        button
            [ class ("button" ++ imgclass)
            , style
                [ ("width", size)
                , ("height", size)
                , ("margin", marg)
                ]
            , onClick clickCmd
            ]
            [ img
                [ class "buttonimg"
                , src ("game-images/" ++ imgfile)
                , style
                    [ ("width", img_size)
                    , ("height", img_size)
                    ]
                ]
                []
            ]

buttons : UiState -> ToolbarDims -> List (Html Msg)
buttons uiState tbdims =
    List.map (viewButton uiState tbdims) buttonsList


viewToolbar : UiState -> {x| toolbar : ToolbarDims } -> Html Msg
viewToolbar uiState dims =
    div
        ([ id "toolbar" ] ++ styles dims.toolbar)
        (buttons uiState dims.toolbar)
