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
import WorldTextRender


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
            , block = Just block
            }
        }


updateLevelClick : Model -> Int -> Int -> Model
updateLevelClick model x y =
    { model
    | world = updateLevelClickWorld model.uiState.block model.world x y
    }


updateLevelClickWorld : Maybe Block -> World -> Int -> Int -> World
updateLevelClickWorld newBlock world x y =
    makeWorld
        world.comment
        (makeBlockGrid
            (List.indexedMap
                (updateLevelClickRow newBlock x y) (blocks world))
        )
        world.rabbits


updateLevelClickRow :
    Maybe Block -> Int -> Int -> Int -> List Block -> List Block
updateLevelClickRow newBlock x y rowy blocks =
    List.indexedMap (updateLevelClickBlock newBlock x y rowy) blocks


updateLevelClickBlock :
    Maybe Block -> Int -> Int -> Int -> Int -> Block -> Block
updateLevelClickBlock newBlock x y rowy colx block =
    if x == colx && y == rowy then
        case newBlock of
            Nothing -> Block Earth Flat
            Just b  -> b
    else
        block


updateChangeMode : Model -> UiMode -> Model
updateChangeMode model mode =
    let
        uiState = model.uiState
        m =
            case mode of
                CodeMode _ -> CodeMode (WorldTextRender.render model.world)
                default -> mode
    in
        { model
        | uiState = { uiState | mode = m }
        }
