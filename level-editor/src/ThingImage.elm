module ThingImage exposing (thingImage)

import Thing exposing (Thing(..), TokenType(..))


thingImage : Maybe Thing -> String
thingImage thing =
    case thing of
        Nothing -> "remove_thing.png"
        Just (Entrance _ _) -> "entrance.png"
        Just (Exit _ _) -> "exit.png"
        Just (Fire _ _) -> "fire.png"
        Just (Token Bash _ _)    -> "token_bash.svg"
        Just (Token Dig _ _)     -> "token_dig.svg"
        Just (Token Bridge _ _)  -> "token_bridge.svg"
        Just (Token BlockT _ _)  -> "token_block.svg"
        Just (Token Climb _ _)   -> "token_climb.svg"
        Just (Token Explode _ _) -> "token_explode.svg"
        Just (Token Brolly _ _)  -> "token_brolly.svg"
