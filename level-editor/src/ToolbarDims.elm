module ToolbarDims exposing (ToolbarDims)


import ToolbarOrientation exposing (ToolbarOrientation)
import Units exposing (..)


type alias ToolbarDims =
    { orientation : ToolbarOrientation
    , thickness : Pixels
    , screenLength : Pixels
    }
