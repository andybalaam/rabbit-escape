module ParseErr exposing (ParseErr(..))


import Item2Text exposing (Pos)


type ParseErr =
      TwoBlocksInOneStarPoint Pos Char Char
    | StarInsideStarPoint Pos
    | UnrecognisedChar Pos Char
    | StarLineDidNotStartWithColonStarEquals Pos String
    | NotEnoughStarLines Pos
    | TooManyStarLines Pos
    | LineWrongLength Pos Int Int
    | MetaParseFailure Pos String String String
    | UnknownMetaName Pos String String
