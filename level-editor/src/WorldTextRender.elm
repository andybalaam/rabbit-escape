module WorldTextRender exposing (render)


import World exposing (Block, Grid(..), World, blocks)


render : World -> String
render world =
    String.join
        "\n"
        (List.map renderLine (blocks world))


renderLine : List Block -> String
renderLine blocks =
    String.concat (List.map renderBlock blocks)


renderBlock : Block -> String
renderBlock block =
    " "
