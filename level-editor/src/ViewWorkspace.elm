module ViewWorkspace exposing (viewWorkspace)

import Html exposing (Html, div)
import Html.Attributes exposing (id)

import Msg exposing (Msg)
import ViewWorld exposing (viewWorld)
import World exposing (World)


viewWorkspace : World -> Html Msg
viewWorkspace world =
    div
        [ id "workspace" ]
        [ viewWorld world ]
