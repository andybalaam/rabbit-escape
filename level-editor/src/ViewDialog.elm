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
        [ onClick (ChangeBlock block) ]
        [ img [ src ("images/" ++ (blockImage block)) ] [] ]


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


blockButtons : UiState -> List (Html Msg)
blockButtons uiState =
    case uiState.mode of
        ChooseBlockMode -> chooseBlockButtons uiState
        default -> []


drawDialog : List (Html Msg) -> List (Html Msg)
drawDialog contents =
    let
        visibleStyle =
            if List.isEmpty contents then
                []
            else
                [style [("visibility", "visible")]]
    in
        [ div
            ( [ id "dialogBackground" ] ++ visibleStyle )
            []
        , div
            ( [ id "dialog" ] ++ visibleStyle )
            contents
        ]


viewDialog : UiState -> List (Html Msg)
viewDialog uiState =
    drawDialog
        ( case uiState.mode of
            ChooseBlockMode -> blockButtons uiState
            default -> []
        )
