module RabbitImage exposing (rabbitImage)

import Rabbit exposing
    ( Direction(..)
    , Rabbit
    , RabbitType(..)
    , makeRabbit
    , makeRabbot
    )


rabbitImage : Maybe Rabbit -> String
rabbitImage rabbit =
    case rabbit of
        Nothing ->
            "remove_rabbit.svg"
        Just r ->
            if r.dir == Left then
                if r.typ == Normal then
                     "rabbit_stand_left.svg"
                else
                     "rabbot_stand_left.svg"
            else
                if r.typ == Normal then
                     "rabbit_stand_right.svg"
                else
                     "rabbot_stand_right.svg"
