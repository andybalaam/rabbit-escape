module ViewToolbar exposing (viewToolbar)

import BlockImage exposing (blockImage)
import Html exposing
    ( Html
    , button
    , div
    , img
    )
import Html.Attributes exposing (class, id, src)
import Html.Events exposing (onClick)

import Msg exposing (Msg(..))
import Model exposing (Model, UiMode(..), UiState)
import World exposing (Block(..), BlockMaterial(..), BlockShape(..))


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
        BlockButton ->
            case uiState.mode of
                ChooseBlockMode -> ChangeMode PlaceBlockMode
                default         -> ChangeMode ChooseBlockMode
        SaveButton  -> ChangeMode (CodeMode "")
        default     -> ChangeMode InitialMode


buttonImage : UiState -> ButtonDef -> String
buttonImage uiState buttondef =
    case buttondef of
        SaveButton -> "save.svg"
        BlockButton ->
            case uiState.block of
                Nothing    -> "allblocks.png"
                Just block -> blockImage block
        RabbitButton -> "rabbit_stand_right.svg"


pressedClass : UiMode -> ButtonDef -> List (Html.Attribute Msg)
pressedClass mode buttondef =
    let
        pressedTypes =
            case mode of
                InitialMode -> []
                CodeMode _ -> [SaveButton]
                ChooseBlockMode -> [BlockButton]
                PlaceBlockMode -> [BlockButton]
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


viewToolbar : Model -> Html Msg
viewToolbar model =
    div
        [ id "toolbar" ]
        (List.map (viewButton model.uiState) buttonsList)
