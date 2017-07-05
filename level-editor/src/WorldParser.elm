module WorldParser exposing (parse)


import World exposing (World, initWorld)


parse : String -> Result String World
parse s =
    Ok initWorld
