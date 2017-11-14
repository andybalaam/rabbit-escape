module Thing exposing (Thing(..), TokenType(..), moved, pos)


type Thing =
      Entrance Int Int
    | Exit Int Int
    | Fire Int Int
    | Token TokenType Int Int


type TokenType =
      Bash
    | Dig
    | Bridge
    | BlockT
    | Climb
    | Explode
    | Brolly


pos : Thing -> (Int, Int)
pos thing =
    case thing of
        Entrance x y -> (x, y)
        Exit x y -> (x, y)
        Fire x y -> (x, y)
        Token _ x y -> (x, y)


moved : Int -> Int -> Thing -> Thing
moved x y thing =
    case thing of
        Entrance _ _ -> Entrance x y
        Exit _ _ -> Exit x y
        Fire _ _ -> Fire x y
        Token tokenType _ _  -> Token tokenType x y
