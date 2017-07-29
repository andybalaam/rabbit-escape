module ViewToolbar exposing (viewToolbar)

import Html exposing
    ( Html
    , div
    , img
    )
import Html.Attributes exposing (class, height, id, src, style, width)


import Msg exposing (Msg)
import Units exposing (..)


viewToolbar : { toolbar_width : Pixels } -> Html Msg
viewToolbar world sq_width =
    let
        blockWidth = 100.0 / toFloat (World.width world)
    in
        div
            [ class "world"
            ]
            (List.concat
                (List.indexedMap
                    (viewRow blockWidth world)
                    (World.blocks world)
                )
            )
