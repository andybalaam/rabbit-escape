module WorldTextRenderTests exposing (all)

import Test exposing (describe,test,Test)
import Expect


import World exposing
    ( World
    , Block(..)
    , BlockMaterial(..)
    , BlockShape(..)
    , makeBlockGrid
    , makeWorld
    )
import WorldTextRender exposing (render)


all : Test
all =
    describe "Tests of the world parser and manipulation"
        [ test "Render empty world" renderEmptyWorld
--        , test "Render world with blocks" renderWorldWithBlocks
        ]


-- ---


renderToLines : World -> List String
renderToLines world =
    String.split "\n" (render world)


fltErth : Block
fltErth =
    Block Earth Flat


-- ---

renderEmptyWorld : () -> Expect.Expectation
renderEmptyWorld =
    \() ->
        Expect.equal
            [ "   "
            , "   "
            , "   "
            ]
            (renderToLines
                (makeWorld
                    "tst"
                    (makeBlockGrid
                        [ [NoBlock, NoBlock, NoBlock]
                        , [NoBlock, NoBlock, NoBlock]
                        , [NoBlock, NoBlock, NoBlock]
                        ]
                    )
                )
            )
