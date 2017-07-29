module View exposing (view)

import Html exposing
    ( Html
    , div
    )
import Html.Attributes exposing (class, id)
import Window


import Model exposing (Model, ModelScreen)
import Msg exposing (Msg(..))
import ToolbarDims exposing (ToolbarDims)
import ToolbarOrientation exposing (ToolbarOrientation(..))
import Units exposing (..)
import ViewToolbar exposing (viewToolbar)
import ViewWorld exposing (viewWorld)
import World exposing (World, width)


toolbarDims : ModelScreen -> World -> ToolbarDims
toolbarDims screen world =
    { orientation =
        if screen.width > screen.height then
            LeftToolbar
        else
            TopToolbar
    , thickness = Pixels ((toFloat (screen.height + screen.width)) / 10.0)
    }


view : Model -> Html Msg
view model =
    let
        screen = model.screen
        world = model.world
        dims =
            { toolbar = toolbarDims screen world
            , square_width = Em 5
            }
    in
        div
            [ id "whole_page"
            ]
            [ (viewToolbar dims)
            , (viewWorld dims world)
            ]
