module Update exposing (update)


import Model exposing (Model, UiMode(..))
import Msg exposing (Msg(..))
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
                LevelClick x y ->
                    updateLevelClick model x y
                ChangeMode mode ->
                    updateChangeMode model mode
                ChangeBlock block ->
                    updateChangeBlock model block
    in
        (m, Cmd.none)


updateChangeBlock : Model -> Block -> Model
updateChangeBlock model block =
    let
        uiState = model.uiState
    in
        { model
        | uiState =
            { uiState
            | mode = PlaceBlockMode
            , block = block
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


updateChangeMode : Model -> UiMode -> Model
updateChangeMode model mode =
    let
        uiState = model.uiState
    in
        { model
        | uiState = { uiState | mode = mode }
        }
