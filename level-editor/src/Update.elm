module Update exposing (update)


import Model exposing (Model)
import Msg exposing (Msg(..))
import Units exposing (..)
import World exposing
    ( Block(..)
    , BlockMaterial(..)
    , BlockShape(..)
    , World
    , blocks
    , makeWorld
    , makeBlockGrid
    )


update : Msg -> Model -> (Model, Cmd Msg)
update msg model =
    let
        m =
            case msg of
                Resize w h ->
                    updateResize model w h
                LevelClick x y ->
                    updateLevelClick model x y
                ChangeMode mode ->
                    model
    in
        (m, Cmd.none)


updateResize : Model -> Int -> Int -> Model
updateResize model w h =
    { model
    | screen =
        { width = Pixels w
        , height = Pixels h
        }
    }


updateLevelClick : Model -> Int -> Int -> Model
updateLevelClick model x y =
    { model | world = updateLevelClickWorld model.world x y }


updateLevelClickWorld : World -> Int -> Int -> World
updateLevelClickWorld world x y =
    makeWorld
        world.comment
        (makeBlockGrid
            (List.indexedMap
                (updateLevelClickRow x y) (blocks world))
        )
        world.rabbits


updateLevelClickRow : Int -> Int -> Int -> List Block -> List Block
updateLevelClickRow x y rowy blocks =
    List.indexedMap (updateLevelClickBlock x y rowy) blocks


updateLevelClickBlock : Int -> Int -> Int -> Int -> Block -> Block
updateLevelClickBlock x y rowy colx block =
    if x == colx && y == rowy then
        Block Earth Flat
    else
        block
