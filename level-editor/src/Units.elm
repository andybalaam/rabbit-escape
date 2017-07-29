module Units exposing (Em(..), Pixels(..), em, px, px_minus)

type Pixels = Pixels Int
type Em = Em Float


(px_minus) : Pixels -> Pixels -> Pixels
(px_minus) (Pixels a) (Pixels b) =
    Pixels (a + b)

em : Em -> String
em (Em f) =
    (toString f) ++ "em"


px : Pixels -> String
px (Pixels i) =
    (toString i) ++ "px"
