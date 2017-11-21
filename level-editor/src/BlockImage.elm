module BlockImage exposing (blockImage)

import World exposing (Block(..), BlockMaterial(..), BlockShape(..))

blockImage : Block -> String
blockImage block =
    case block of
        Block Earth Flat          -> "land_block_1.png"
        Block Earth UpRight       -> "land_rising_right_1.png"
        Block Earth UpLeft        -> "land_rising_left_1.png"
        Block Earth BridgeUpRight -> "bridge_rising_right.svg"
        Block Earth BridgeUpLeft  -> "bridge_rising_left.svg"
        Block Metal _             -> "metal_block_1.png"
        NoBlock                   -> "remove_block.png"
