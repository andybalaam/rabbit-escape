module MetaLines exposing
    ( MetaLines
    , MetaValue(..)
    , ValueExtract
    , ValueInsert
    , defaultMeta
    , valuesList
    )


type MetaValue a =
    MetaValue a


type alias ValueExtract = MetaLines -> MetaValue Int
type alias ValueInsert a = MetaLines -> MetaValue a -> MetaLines


type alias MetaLines =
    { num_rabbits : MetaValue Int
    , num_to_save : MetaValue Int
    }


defaultMeta : MetaLines
defaultMeta =
    { num_rabbits = MetaValue 10
    , num_to_save = MetaValue 1
    }


valuesList : List (String, ValueExtract, ValueInsert Int)
valuesList =
    [ ("num_rabbits", .num_rabbits, \l v -> {l|num_rabbits=v})
    , ("num_to_save", .num_to_save, \l v -> {l|num_to_save=v})
    ]
