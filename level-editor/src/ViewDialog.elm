module ViewDialog exposing (viewDialog)

import BlockImage exposing (blockImage)
import Html exposing (Html, button, div, img)
import Html.Attributes exposing (id, src, style)
import Html.Events exposing (onClick)
import Model exposing (UiMode(..), UiState)
import Msg exposing (Msg(..))
import World exposing (Block(..), BlockMaterial(..), BlockShape(..))


but : Block -> Html Msg
but block =
    button
        []
        [ img
            [ src ("images/" ++ (blockImage block))
            , onClick (ChangeBlock block)
            ]
            []
        ]


chooseBlockButtons : UiState -> List (Html Msg)
chooseBlockButtons state =
    [ but (Block Earth Flat)
    , but (Block Earth UpRight)
    , but (Block Earth UpLeft)
    , but (Block Earth BridgeUpRight)
    , but (Block Earth BridgeUpLeft)
    , but (Block Metal Flat)
    , but (NoBlock)
    ]


buttons : UiState -> List (Html Msg)
buttons uiState =
    case uiState.mode of
        ChooseBlockMode -> chooseBlockButtons uiState
        default -> []


styles : Bool -> List (Html.Attribute Msg)
styles visible =
    if visible then
        [style [("visibility", "visible")]]
    else
        []


viewDialog : UiState -> List (Html Msg)
viewDialog uiState =
    let
        visible =
            case uiState.mode of
                ChooseBlockMode -> True
                default -> False
        visibleStyle = styles visible
    in
        [ div
            ( [ id "dialogBackground"
              ] ++ visibleStyle
            )
            [
            ]
        , div
            ( [ id "dialog"
              ] ++ visibleStyle
            )
            (buttons uiState)
        ]
