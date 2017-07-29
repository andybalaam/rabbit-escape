module Units exposing (..)

type Pixels = Pixels Int


(.+.) : Pixels -> Pixels -> Pixels
(.+.) (Pixels a) (Pixels b) =
    Pixels (a + b)


(.-.) : Pixels -> Pixels -> Pixels
(.-.) (Pixels a) (Pixels b) =
    Pixels (a - b)


(.*.) : Pixels -> Int -> Pixels
(.*.) (Pixels a) b =
    Pixels (a * b)


(.**.) : Pixels -> Float -> Pixels
(.**.) (Pixels a) b =
    Pixels (round (toFloat a * b))


(./.) : Pixels -> Int -> Pixels
(./.) (Pixels a) b =
    Pixels (round (toFloat a / toFloat b))


(.>.) : Pixels -> Pixels -> Bool
(.>.) (Pixels a) (Pixels b) =
    a > b


(.<.) : Pixels -> Pixels -> Bool
(.<.) (Pixels a) (Pixels b) =
    a < b


px : Pixels -> String
px (Pixels i) =
    (toString i) ++ "px"


-- ---


type Em = Em Float


(:*:) : Em -> Int -> Em
(:*:) (Em a) b =
    Em (a * (toFloat b))


em : Em -> String
em (Em f) =
    (toString f) ++ "em"
