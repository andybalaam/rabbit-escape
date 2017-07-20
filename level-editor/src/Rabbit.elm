module Rabbit exposing (Direction(..), Rabbit, makeRabbit)


type Direction =
    Left | Right


type alias Rabbit =
    { x : Int
    , y : Int
    , dir : Direction
    }


makeRabbit : Int -> Int -> Direction -> Rabbit
makeRabbit x y dir =
    { x = x
    , y = y
    , dir = dir
    }
