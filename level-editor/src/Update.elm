module Update exposing (update)


import Model exposing (Model, UiMode(..))
import Msg exposing (Msg(..))
import Rabbit exposing (Rabbit, movedRabbit)
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

        updatedModel =
            case msg of
                Undo -> updateUndo model
                Redo -> updateRedo model
                default -> normalUpdate msg model
    in
        (updatedModel, Cmd.none)


normalUpdate : Msg -> Model -> Model
normalUpdate msg model =
    let

        updatedModel =
            case msg of
                LevelClick x y ->
                    case model.uiState.mode of
                        InitialMode ->
                            updateLevelClickBlock model x y
                        PlaceBlockMode ->
                            updateLevelClickBlock model x y
                        PlaceRabbitMode ->
                            updateLevelClickRabbit model x y
                        default ->
                            model
                ChangeMode mode ->
                    updateChangeMode model mode
                ChangeBlock block ->
                    updateChangeBlock model block
                ChangeRabbit rabbit ->
                    updateChangeRabbit model rabbit
                Undo ->
                    crashBadMessage msg
                Redo ->
                    crashBadMessage msg
    in
        -- If something changed, remember in undo stack.
        if updatedModel.world == model.world then
            updatedModel
        else
            { updatedModel | past = model.world :: model.past, future = [] }


crashBadMessage : Msg -> Model
crashBadMessage msg =
    Debug.crash -- yuck
        ( "Msg '" ++ toString msg ++ "' should not "
        ++ "be passed in to normalUpdate."
        )


updateUndo : Model -> Model
updateUndo model =
    case model.past of
        [] -> model
        recent :: others ->
            { model
            | world = recent
            , past = others
            , future = model.world :: model.future
            }


updateRedo : Model -> Model
updateRedo model =
    case model.future of
        [] -> model
        recent :: others ->
            { model
            | world = recent
            , future = others
            , past = model.world :: model.past
            }


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


updateChangeRabbit : Model -> Maybe Rabbit -> Model
updateChangeRabbit model rabbit =
    let
        uiState = model.uiState
    in
        { model
        | uiState =
            { uiState
            | mode = PlaceRabbitMode
            , rabbit = rabbit
            }
        }


updateLevelClickRabbit : Model -> Int -> Int -> Model
updateLevelClickRabbit model x y =
    { model
    | world = updateLevelClickRabbitWorld model.uiState.rabbit model.world x y
    }


updateLevelClickRabbitWorld : Maybe Rabbit -> World -> Int -> Int -> World
updateLevelClickRabbitWorld newRabbit world x y =
    let
        rabbits =
            case newRabbit of
                Nothing ->
                    List.filter
                        (\rabbit -> rabbit.x /= x || rabbit.y /= y)
                        world.rabbits
                Just r ->
                    movedRabbit x y r :: world.rabbits
    in
        makeWorld world.comment world.blocks rabbits world.things


updateLevelClickBlock : Model -> Int -> Int -> Model
updateLevelClickBlock model x y =
    { model
    | world = updateLevelClickBlockWorld model.uiState.block model.world x y
    }


updateLevelClickBlockWorld : Maybe Block -> World -> Int -> Int -> World
updateLevelClickBlockWorld newBlock world x y =
    makeWorld
        world.comment
        (makeBlockGrid
            (List.indexedMap
                (updateLevelClickBlockRow newBlock x y) (blocks world))
        )
        world.rabbits
        world.things


updateLevelClickBlockRow :
    Maybe Block -> Int -> Int -> Int -> List Block -> List Block
updateLevelClickBlockRow newBlock x y rowy blocks =
    List.indexedMap (updateLevelClickBlockBlock newBlock x y rowy) blocks


updateLevelClickBlockBlock :
    Maybe Block -> Int -> Int -> Int -> Int -> Block -> Block
updateLevelClickBlockBlock newBlock x y rowy colx block =
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
