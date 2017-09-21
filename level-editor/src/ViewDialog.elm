module ViewDialog exposing (viewDialog)

import Html exposing (Html, button, div, img)
import Html.Attributes exposing (id, src, style)
import Html.Events exposing (onClick)
import Model exposing (UiMode(..), UiState)
import Msg exposing (Msg(..))


buttons : UiState -> List (Html Msg)
buttons uiState =
    []


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
