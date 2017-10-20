module ViewToolbar exposing (viewToolbar)

import BlockImage exposing (blockImage)
import Html exposing
    ( Html
    , button
    , div
    , img
    )
import Html.Attributes exposing (class, disabled, id, src)
import Html.Events exposing (onClick)

import Msg exposing (Msg(..))
import Model exposing (Model, UiMode(..), UiState)
import World exposing (Block(..), BlockMaterial(..), BlockShape(..))


type ButtonDef =
      SaveButton
    | UndoButton
    | RedoButton
    | BlockButton
    | RabbitButton


buttonsList : List ButtonDef
buttonsList =
    [ SaveButton
    , UndoButton
    , RedoButton
    , BlockButton
    , RabbitButton
    ]


buildClickCmd : UiState -> ButtonDef -> Msg
buildClickCmd uiState buttonDef =
    case buttonDef of
        SaveButton  ->
            case uiState.mode of
                CodeMode _ -> ChangeMode InitialMode
                default    -> ChangeMode (CodeMode "")
        UndoButton ->
            Undo
        RedoButton ->
            Redo
        BlockButton ->
            case uiState.mode of
                ChooseBlockMode -> ChangeMode PlaceBlockMode
                default         -> ChangeMode ChooseBlockMode
        RabbitButton ->
            case uiState.mode of
                ChooseRabbitMode -> ChangeMode PlaceRabbitMode
                default          -> ChangeMode ChooseRabbitMode


buttonImage : UiState -> ButtonDef -> String
buttonImage uiState buttondef =
    case buttondef of
        SaveButton -> "save.svg"
        UndoButton -> "undo.svg"
        RedoButton -> "redo.svg"
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
                ChooseRabbitMode -> [RabbitButton]
                PlaceRabbitMode -> [RabbitButton]
    in
        if List.member buttondef pressedTypes then
            [ class "pressed" ]
        else
            []


buttonEnabled : Model -> ButtonDef -> List (Html.Attribute Msg)
buttonEnabled model buttondef =
    if
        case model.uiState.mode of
            CodeMode _ ->
                case buttondef of
                    SaveButton -> False
                    default -> True
            default ->
                case buttondef of
                    UndoButton -> List.isEmpty model.past
                    RedoButton -> List.isEmpty model.future
                    default -> False
    then
        [disabled True]
    else
        []


viewButton : Model -> ButtonDef -> Html Msg
viewButton model def =
    button
        ( [ onClick (buildClickCmd model.uiState def) ]
          ++ pressedClass model.uiState.mode def
          ++ buttonEnabled model def
        )
        [ img
            [ src ("images/" ++ (buttonImage model.uiState def)) ]
            []
        ]


viewToolbar : Model -> Html Msg
viewToolbar model =
    div
        [ id "toolbar" ]
        (List.map (viewButton model) buttonsList)
