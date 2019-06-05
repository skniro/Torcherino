package torcherino.api;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import torcherino.api.impl.TorcherinoBlacklistImpl;

public interface TorcherinoBlacklistAPI
{
	TorcherinoBlacklistAPI INSTANCE = TorcherinoBlacklistImpl.INSTANCE;

	boolean isBlockBlacklisted(Block block);

	boolean isBlockEntityBlacklisted(BlockEntityType blockEntityType);

	void blacklistBlock(Block block);

	void blacklistBlockEntity(BlockEntityType blockEntityType);

	void blacklistIdentifier(Identifier identifier);
}
