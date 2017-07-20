module WorldTextRender exposing (render)


import Item2Text exposing (toText)
import World exposing (Block, Grid(..), World, blocks, rabbitsAt)


render : World -> String
render world =
    String.join
        "\n"
        (List.indexedMap (renderLine world) (blocks world))


toText2 : World -> Int -> Int -> Block -> Char
toText2 world y x block =
    toText block (rabbitsAt world x y)


renderLine : World -> Int -> List Block -> String
renderLine world y blocks =
    String.fromList (List.indexedMap (toText2 world y) blocks)
