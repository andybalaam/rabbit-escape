module Thing exposing (Thing(..), moved, pos)


type Thing =
      Entrance Int Int
    | Exit Int Int


pos : Thing -> (Int, Int)
pos thing =
    case thing of
        Entrance x y -> (x, y)
        Exit x y -> (x, y)


moved : Int -> Int -> Thing -> Thing
moved x y thing =
    case thing of
        Entrance _ _ -> Entrance x y
        Exit _ _ -> Exit x y
