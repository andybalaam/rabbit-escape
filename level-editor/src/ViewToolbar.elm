module ViewToolbar exposing (viewToolbar)

import Html exposing
    ( Html
    , button
    , div
    , img
    )
import Html.Attributes exposing (class, id, src)
import Html.Events exposing (onClick)

import Msg exposing (Msg(..))
import Model exposing (UiMode(..), UiState)


type ButtonDef =
      SaveButton
    | BlockButton
    | RabbitButton


buttonsList : List ButtonDef
buttonsList =
    [ SaveButton
    , BlockButton
    , RabbitButton
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
        BlockButton -> "allblocks.png"
        RabbitButton -> "rabbit_stand_right.svg"


pressedClass : UiMode -> ButtonDef -> List (Html.Attribute Msg)
pressedClass mode buttondef =
    let
        pressedTypes =
            case mode of
                InitialMode -> []
                ChooseBlockMode -> [BlockButton]
    in
        if List.member buttondef pressedTypes then
            [ class "pressed" ]
        else
            []


viewButton : UiState -> ButtonDef -> Html Msg
viewButton uiState def =
    button
        ( [ onClick (buildClickCmd uiState def)
          ] ++ pressedClass uiState.mode def
        )
        [ img
            [ src ("images/" ++ (buttonImage uiState def)) ]
            []
        ]


viewToolbar : UiState -> Html Msg
viewToolbar uiState =
    div
        [ id "toolbar" ]
        (List.map (viewButton uiState) buttonsList)
