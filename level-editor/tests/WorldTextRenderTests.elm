module WorldTextRenderTests exposing (all)

import Test exposing (describe,test,Test)
import Expect


import Rabbit exposing (Direction(..), Rabbit, makeRabbit)
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
        , test "Render world with blocks" renderWorldWithBlocks
        , test "Render world with rabbits" renderWorldWithRabbits
        ]


-- ---


renderToLines : World -> List String
renderToLines world =
    String.split "\n" (render world)


fltErth : Block
fltErth =
    Block Earth Flat


rend : List (List Block) -> List Rabbit -> List String
rend blocks rabbits =
    renderToLines (makeWorld "tst" (makeBlockGrid blocks) rabbits)


-- ---

renderEmptyWorld : () -> Expect.Expectation
renderEmptyWorld =
    \() ->
        Expect.equal
            [ "   "
            , "   "
            , "   "
            ]
            (rend
                [ [NoBlock, NoBlock, NoBlock]
                , [NoBlock, NoBlock, NoBlock]
                , [NoBlock, NoBlock, NoBlock]
                ]
                []
            )


renderWorldWithBlocks : () -> Expect.Expectation
renderWorldWithBlocks =
    \() ->
        Expect.equal
            [ "    "
            , "   #"
            , "    "
            , "####"
            ]
            (rend
                [ [NoBlock, NoBlock, NoBlock, NoBlock]
                , [NoBlock, NoBlock, NoBlock, fltErth]
                , [NoBlock, NoBlock, NoBlock, NoBlock]
                , [fltErth, fltErth, fltErth, fltErth]
                ]
                []
            )


renderWorldWithRabbits : () -> Expect.Expectation
renderWorldWithRabbits =
    \() ->
        Expect.equal
            [ "    "
            , "  r#"
            , " j  "
            , "####"
            ]
            (rend
                [ [NoBlock, NoBlock, NoBlock, NoBlock]
                , [NoBlock, NoBlock, NoBlock, fltErth]
                , [NoBlock, NoBlock, NoBlock, NoBlock]
                , [fltErth, fltErth, fltErth, fltErth]
                ]
                [ makeRabbit 2 1 Right
                , makeRabbit 1 2 Left
                ]
            )
