module Rabbit exposing
    ( Direction(..)
    , Rabbit
    , RabbitType(..)
    , makeRabbit
    , makeRabbot
    , movedRabbit
    )


type RabbitType =
    Normal | Rabbot


type Direction =
    Left | Right


type alias Rabbit =
    { x : Int
    , y : Int
    , dir : Direction
    , typ : RabbitType
    }


makeRabbit : Int -> Int -> Direction -> Rabbit
makeRabbit x y dir =
    { x = x
    , y = y
    , dir = dir
    , typ = Normal
    }


makeRabbot : Int -> Int -> Direction -> Rabbit
makeRabbot x y dir =
    { x = x
    , y = y
    , dir = dir
    , typ = Rabbot
    }


movedRabbit : Int -> Int -> Rabbit -> Rabbit
movedRabbit x y rabbit =
    { rabbit | x = x, y = y }
