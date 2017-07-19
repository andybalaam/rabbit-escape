module WorldTextRender exposing (render)


import Item2Text exposing (toText)
import World exposing (Block, Grid(..), World, blocks)


render : World -> String
render world =
    String.join
        "\n"
        (List.map renderLine (blocks world))


renderLine : List Block -> String
renderLine blocks =
    String.fromList (List.map toText blocks)
