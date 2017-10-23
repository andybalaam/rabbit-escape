module ThingImage exposing (thingImage)

import Thing exposing (Thing(..))


thingImage : Maybe Thing -> String
thingImage thing =
    case thing of
        Nothing ->
            "remove_thing.png"
        Just (Entrance _ _) -> "entrance.png"
        Just (Exit _ _) -> "exit.png"
