module MetaLinesTests exposing (all)


import Dict
import Test exposing (test, Test)
import Expect


import MetaLines exposing (..)


all : Test
all = Test.concat
    [ assert_contains "Convert to list of string"
        ("num_rabbits", "10")
        (toStringList defaults)

    , assert_contains "Parse a correct single int value"
        ("num_to_save", "4")
        ( case parseAndSet "num_to_save" "4" defaults of
            Ok ml -> toStringList ml
            _ -> [("Failed to parse!", "")]
        )

    , eq "Convert to list of non-defaults is empty for default"
        []
        (toNonDefaultStringList defaults)

    , eq "Changed values appear in non-default list"
        ( Ok
            [ ("name", "Lev !")
            , ("num_to_save", "2")
            ]
        )
        ( parseAndSet "num_to_save" "2" defaults
            |> Result.andThen (parseAndSet "name" "Lev !")
            |> Result.map toNonDefaultStringList
        )

    , assert_contains "Parse a correct single string value"
        ("name", "My Level")
        ( case parseAndSet "name" "My Level" defaults of
            Ok ml -> toStringList ml
            _ -> [("Failed to parse!", "")]
        )

    , assert_contains "Default values stay after others are set"
        ("num_to_save", "1")
        ( case parseAndSet "num_rabbits" "34" defaults of
            Ok ml -> toStringList ml
            _ -> [("Failed to parse!", "")]
        )

    , eq "Setting an unknown value is an error"
        (parseAndSet "custom_field" "foo" defaults)
        (Err (UnknownName "custom_field"))

    , eq "Parsing a non-int into an int value is an error"
        (parseAndSet "num_rabbits" "foo" defaults)
        (Err (BadValue "num_rabbits" "foo"))

    , eq "Applying an empty diff does nothing"
        (applyDiff emptyDiff defaults)
        defaults

    , eq "Convert diff to list"
        (toDiffList (setDiff "num_rabbits" "2" emptyDiff))
        [("num_rabbits", {raw="2", parsed=Ok (MvInt 2)})]

    , eq "Get a value from a diff"
        ( getDiff "num_rabbits"
            ( setDiff
                "num_rabbits"
                "2"
                (setDiff "num_to_save" "1" emptyDiff)
            )
        )
        (Just {raw="2", parsed=Ok (MvInt 2)})

    , eq "Get a missing value from a diff"
        ( getDiff "num_rabbits"
            ( setDiff "num_to_save" "1" emptyDiff)
        )
        Nothing

    , eq "A valid diff is all OK"
        ( allOk
            ( setDiff
                "num_rabbits"
                "2"
                (setDiff "num_to_save" "1" emptyDiff)
            )
        )
        True

    , eq "An invalid diff is not all OK"
        ( allOk
            ( setDiff
                "num_rabbits"
                "2"
                (setDiff "num_to_save" "all" emptyDiff)
            )
        )
        False

    , assert_contains "Applying a valid diff updates the lines"
        ("num_rabbits", "3")
        ( toStringList
            (applyDiff (setDiff "num_rabbits" "3" emptyDiff) defaults)
        )

    , assert_contains "Applying an invalid diff makes no change"
        ("num_rabbits", "10")
        ( toStringList
            (applyDiff (setDiff "num_rabbits" "BAD_INT" emptyDiff) defaults)
        )

    , assert_contains "Applying valid and invalid values applies the valid"
        ("num_rabbits", "3")
        ( toStringList
            (applyDiff
                (setDiff "num_rabbits" "3"
                    (setDiff "num_to_save" "BAD"
                        (setDiff "name" "Ma Lev" emptyDiff)
                    )
                )
                defaults
            )
        )
    ]


listContains : a -> List a -> Expect.Expectation
listContains item list =
    if List.member item list then
        Expect.pass
    else
        Expect.fail
            (  "listContains: the list: "
            ++ Debug.toString list
            ++ " does not contain: "
            ++ Debug.toString item
            ++ "."
            )


eq : String -> a -> a -> Test
eq desc act exp =
    test desc (\() -> Expect.equal act exp)

assert_contains : String -> a -> List a -> Test
assert_contains desc item list =
    test desc (\() -> listContains item list)
