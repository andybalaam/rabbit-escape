module ViewDialog exposing (viewDialog)

import Html exposing (Html, button, div, img)
import Html.Attributes exposing (id, src, style)
import Html.Events exposing (onClick)
import Model exposing (UiMode(..), UiState)
import Msg exposing (Msg(..))
import World exposing (Block(..), BlockMaterial(..), BlockShape(..))


but : Msg -> String -> Html Msg
but changeBlock imageName =
    button
        []
        [ img
            [ src ("images/" ++ imageName) ]
            []
        ]


chooseBlockButtons : UiState -> List (Html Msg)
chooseBlockButtons state =
    [ but (ChangeBlock (Block Earth Flat))    "land_block_1.png"
    , but (ChangeBlock (Block Earth UpRight)) "land_rising_right_1.png"
    , but (ChangeBlock (Block Earth UpLeft))  "land_rising_left_1.png"
    , but (ChangeBlock (Block Metal Flat))    "metal_block_1.png"
    , but (ChangeBlock (NoBlock))             "remove_block.png"
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
