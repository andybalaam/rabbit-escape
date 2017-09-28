module ViewDialog exposing (viewDialog)

import BlockImage exposing (blockImage)
import Html exposing (Html, button, div, img, text, textarea)
import Html.Attributes exposing (id, src, style)
import Html.Events exposing (onClick)
import Model exposing (Model, UiMode(..), UiState)
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


codeText : String -> List (Html Msg)
codeText code =
    [ textarea
        [ id "code" ]
        [ text code ]
    ]


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


viewDialog : Model -> List (Html Msg)
viewDialog model =
    drawDialog
        ( case model.uiState.mode of
            ChooseBlockMode -> chooseBlockButtons model.uiState
            CodeMode code -> codeText code
            default -> []
        )
