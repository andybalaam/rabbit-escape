module View exposing (view)

import Html exposing
    ( Html
    , div
    )
import Html.Attributes exposing (id)
import Model exposing (Model)
import Msg exposing (Msg)
import ViewDialog exposing (viewDialog)
import ViewToolbar exposing (viewToolbar)
import ViewWorkspace exposing (viewWorkspace)


view : Model -> Html Msg
view model =
    div
        [ id "main"
        ]
        (
            [ (viewToolbar model)
            , (viewWorkspace model.world)
            ] ++ (viewDialog model)
        )
