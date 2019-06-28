package torcherino.api.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.INameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import torcherino.Utilities;
import torcherino.api.Tier;
import torcherino.config.Config;
import torcherino.network.Networker;
import javax.annotation.Nullable;
import java.util.Random;

@SuppressWarnings("deprecation")
public class TorcherinoBlock extends BlockTorch
{
	// Constructors
	public TorcherinoBlock(Tier tier)
	{
		super(Properties.from(Blocks.TORCH));
		this.tier = tier;
	}

	// Variables
	private static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	private final Tier tier;

	// Methods
	public Tier getTier(){ return tier; }

	@Override public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Nullable @Override public TileEntity createTileEntity(IBlockState state, IBlockReader world)
	{
		return new TorcherinoTileEntity();
	}

	@Override protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder)
	{
		super.fillStateContainer(builder);
		builder.add(POWERED);
	}

	@Override public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		return Networker.INSTANCE.openScreenServer(world, player, pos);
	}

	@Override public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack)
	{
		if(world.isRemote) return;
		if (stack.hasDisplayName())
		{
			TileEntity tile = world.getTileEntity(pos);
			if (!(tile instanceof TorcherinoTileEntity)) return;
			((TorcherinoTileEntity) tile).setCustomName(stack.getDisplayName());
		}
		if(Config.INSTANCE.log_placement)
		{
			String prefix = "Something";
			if (placer != null) prefix = placer.getDisplayName().getString() + "(" + placer.getCachedUniqueIdString() + ")";
			Utilities.LOGGER.info("[Torcherino] {} placed a {} at {} {} {}.", prefix, StringUtils.capitalize(getTranslationKey().replace("block.torcherino.", "").replace("_", " ")), pos.getX(), pos.getY(), pos.getZ());
		}
	}

	@Override public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack)
	{
		if (te instanceof INameable && ((INameable) te).hasCustomName())
		{
			player.addStat(StatList.BLOCK_MINED.get(this));
			player.addExhaustion(0.005F);
			if (world.isRemote) return;
			int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
			Item item = this.getItemDropped(state, world, pos, fortune).asItem();
			if (item == Items.AIR) return;
			int itemsDropped = this.getItemsToDropCount(state, fortune, world, pos, world.rand);
			ItemStack itemstack = new ItemStack(item, itemsDropped);
			itemstack.setDisplayName(((INameable) te).getCustomName());
			spawnAsEntity(world, pos, itemstack);
		}
		else
		{
			super.harvestBlock(world, player, pos, state, null, stack);
		}
	}

	@Override public void tick(IBlockState state, World world, BlockPos pos, Random random)
	{
		if(world.isRemote) return;
		TileEntity tileEntity = world.getTileEntity(pos);
		if(tileEntity instanceof TorcherinoTileEntity) ((TorcherinoTileEntity) tileEntity).tick();
	}

	@Override public EnumPushReaction getPushReaction(IBlockState state){ return EnumPushReaction.IGNORE; }

	@Override public void onBlockAdded(IBlockState state, World world, BlockPos pos, IBlockState oldState)
	{
		TileEntity tileEntity = world.getTileEntity(pos);
		if(tileEntity instanceof TorcherinoTileEntity) ((TorcherinoTileEntity) tileEntity).setPoweredByRedstone(state.get(POWERED));
	}

	// Unique Methods ( can't be copy / pasted between torcherino classes )
	@Override public IBlockState getStateForPlacement(BlockItemUseContext context)
	{
		boolean powered = context.getWorld().isBlockPowered(context.getPos().down());
		return getDefaultState().with(POWERED, powered);
	}

	@Override public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		if(world.isRemote) return;
		boolean powered = world.isBlockPowered(pos.down());
		if (state.get(POWERED) != powered)
		{
			world.setBlockState(pos, state.with(POWERED, powered));
			TileEntity tileEntity = world.getTileEntity(pos);
			if(tileEntity instanceof TorcherinoTileEntity)
			{
				((TorcherinoTileEntity) tileEntity).setPoweredByRedstone(powered);
			}
		}
	}
}
