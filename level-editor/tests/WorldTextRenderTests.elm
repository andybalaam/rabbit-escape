module WorldTextRenderTests exposing (all)

import Test exposing (describe,test,Test)
import Expect


import MetaLines exposing (MetaLines, MetaValue(..))
import Rabbit exposing (Direction(..), Rabbit, makeRabbit, makeRabbot)
import Thing exposing (Thing(..))
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
    describe "Tests of the world renderer"
        [ t "Render empty world"
            [ [NoBlock, NoBlock, NoBlock]
            , [NoBlock, NoBlock, NoBlock]
            , [NoBlock, NoBlock, NoBlock]
            ] [] [] MetaLines.defaults
            [ "   "
            , "   "
            , "   "
            ]

        , t "Render world with blocks"
            [ [NoBlock, NoBlock, NoBlock, NoBlock]
            , [NoBlock, NoBlock, NoBlock, fltErth]
            , [NoBlock, NoBlock, NoBlock, NoBlock]
            , [fltErth, fltErth, fltErth, fltErth]
            ] [] [] MetaLines.defaults
            [ "    "
            , "   #"
            , "    "
            , "####"
            ]

        , t "Render world with rabbits"
            [ [NoBlock, NoBlock, NoBlock, NoBlock]
            , [NoBlock, NoBlock, NoBlock, fltErth]
            , [NoBlock, NoBlock, NoBlock, NoBlock]
            , [fltErth, fltErth, fltErth, fltErth]
            ]
            [ makeRabbot 0 1 Right
            , makeRabbot 0 0 Left
            , makeRabbit 2 1 Right
            , makeRabbit 1 2 Left
            ] [] MetaLines.defaults
            [ "y   "
            , "t r#"
            , " j  "
            , "####"
            ]

        , t "Render world with things"
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
            [ Entrance 1 0
            , Exit 3 0
            ]
            MetaLines.defaults
            [ "yQ O"
            , "t r#"
            , " j  "
            , "####"
            ]

        , t "Render 2 rabbits in same place"
            [ [NoBlock, NoBlock, NoBlock, NoBlock]
            , [NoBlock, NoBlock, NoBlock, NoBlock]
            , [NoBlock, NoBlock, NoBlock, NoBlock]
            , [NoBlock, NoBlock, NoBlock, NoBlock]
            ]
            [ makeRabbit 2 1 Right
            , makeRabbit 2 1 Left
            ] [] MetaLines.defaults
            [ "    "
            , "  * "
            , "    "
            , "    "
            , ":*=rj"
            ]

        , t "Render multiple things in same place"
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
            [ Entrance 2 1
            , Exit 3 1
            ]
            MetaLines.defaults
            [ "    "
            , "  **"
            , "*   "
            , "    "
            , ":*=#rjQ"
            , ":*=MrO"
            , ":*=/j"
            ]

        , t "Render meta-lines"
            [ [NoBlock]
            , [uprErth]
            ]
            []
            []
            ( MetaLines.fromList
                [ ("num_rabbits", MvInt 4)
                , ("num_to_save", MvInt 2)
                ]
            )
            [ " "
            , "/"
            , ":num_rabbits=4"
            , ":num_to_save=2"
            ]
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


rend
    : List (List Block)
    -> List Rabbit
    -> List Thing
    -> MetaLines
    -> List String
rend blocks rabbits things metaLines =
    renderToLines
        (makeWorld "tst" (makeBlockGrid blocks) rabbits things metaLines)


t
    : String
    -> List (List Block)
    -> List Rabbit
    -> List Thing
    -> MetaLines
    -> List String
    -> Test
t desc blocks rabbits things metaLines expected =
    test
        desc
        ( \() ->
            Expect.equal expected (rend blocks rabbits things metaLines)
        )
