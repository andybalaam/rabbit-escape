module WorldTextRenderTests exposing (all)

import Test exposing (describe,test,Test)
import Expect


import Rabbit exposing (Direction(..), Rabbit, makeRabbit, makeRabbot)
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
        , test "Render 2 rabbits in same place" render2RabbitsInSamePlace
        , test "Render rabbit on top of block" renderRabbitOnBlock
        ]


-- ---


renderToLines : World -> List String
renderToLines world =
    String.split "\n" (render world)


fltErth : Block
fltErth =
    Block Earth Flat


fltMetl : Block
fltMetl =
    Block Metal Flat


uprErth : Block
uprErth =
    Block Earth UpRight


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
            [ "y   "
            , "t r#"
            , " j  "
            , "####"
            ]
            (rend
                [ [NoBlock, NoBlock, NoBlock, NoBlock]
                , [NoBlock, NoBlock, NoBlock, fltErth]
                , [NoBlock, NoBlock, NoBlock, NoBlock]
                , [fltErth, fltErth, fltErth, fltErth]
                ]
                [ makeRabbot 0 1 Right
                , makeRabbot 0 0 Left
                , makeRabbit 2 1 Right
                , makeRabbit 1 2 Left
                ]
            )


render2RabbitsInSamePlace : () -> Expect.Expectation
render2RabbitsInSamePlace =
    \() ->
        Expect.equal
            [ "    "
            , "  * "
            , "    "
            , "    "
            , ":*=rj"
            ]
            (rend
                [ [NoBlock, NoBlock, NoBlock, NoBlock]
                , [NoBlock, NoBlock, NoBlock, NoBlock]
                , [NoBlock, NoBlock, NoBlock, NoBlock]
                , [NoBlock, NoBlock, NoBlock, NoBlock]
                ]
                [ makeRabbit 2 1 Right
                , makeRabbit 2 1 Left
                ]
            )


renderRabbitOnBlock : () -> Expect.Expectation
renderRabbitOnBlock =
    \() ->
        Expect.equal
            [ "    "
            , "  **"
            , "*   "
            , "    "
            , ":*=#rj"
            , ":*=Mr"
            , ":*=/j"
            ]
            (rend
                [ [NoBlock, NoBlock, NoBlock, NoBlock]
                , [NoBlock, NoBlock, fltErth, fltMetl]
                , [uprErth, NoBlock, NoBlock, NoBlock]
                , [NoBlock, NoBlock, NoBlock, NoBlock]
                ]
                [ makeRabbit 2 1 Right
                , makeRabbit 2 1 Left
                , makeRabbit 3 1 Right
                , makeRabbit 0 2 Left
                ]
            )
