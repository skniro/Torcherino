package torcherino.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.commons.lang3.StringUtils;
import torcherino.Utils;
import torcherino.blocks.misc.TorcherinoTileEntity;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public class BlockTorcherino extends BlockTorch
{
	private int MAX_SPEED;

	public BlockTorcherino(int speed)
	{
		super(Properties.create(Material.CIRCUITS).doesNotBlockMovement().hardnessAndResistance(0).lightValue(14).sound(SoundType.WOOD));
		MAX_SPEED = speed;
	}

	@Override public boolean hasTileEntity(IBlockState state){ return true; }

	@Override public TileEntity createTileEntity(IBlockState state, IBlockReader world){ return new TorcherinoTileEntity(MAX_SPEED); }

	@Override @ParametersAreNonnullByDefault public void onBlockAdded(IBlockState state, World world, BlockPos blockPos, IBlockState oldState){ neighborChanged(state, world, blockPos, null, null); }

	@Override @Nonnull public EnumPushReaction getPushReaction(@Nonnull IBlockState state){ return EnumPushReaction.IGNORE; }

	@Override @ParametersAreNonnullByDefault public void neighborChanged(IBlockState selfState, World world, BlockPos selfPos, Block neighborBlock, BlockPos neighborPos)
	{
		if (world.isRemote) return;
		TileEntity tileEntity = world.getTileEntity(selfPos);
		if (tileEntity == null) return;
		((TorcherinoTileEntity) tileEntity).setPoweredByRedstone(world.isSidePowered(selfPos.down(), EnumFacing.DOWN));
	}

	@Override @ParametersAreNonnullByDefault public void tick(IBlockState state, World world, BlockPos pos, Random random)
	{
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof ITickable) ((ITickable) tileEntity).tick();
	}

	@Override @ParametersAreNonnullByDefault public void onReplaced(IBlockState state, World world, BlockPos pos, IBlockState state1, boolean b)
	{
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity != null) tileEntity.remove();
	}

	@Override @ParametersAreNonnullByDefault public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack)
	{
		if (world.isRemote) return;
		if (!Utils.logPlacement)
		{
			String prefix = "Something";
			if (placer != null) prefix = placer.getDisplayName().getString() + "(" + placer.getCachedUniqueIdString() + ")";
			Utils.LOGGER.info("[Torcherino] {} placed a {} at {} {} {}.", prefix, StringUtils.capitalize(getTranslationKey().replace("block.torcherino.", "").replace("_", " ")), pos.getX(), pos.getY(), pos.getZ());
		}
		if (stack.hasDisplayName())
		{
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof TorcherinoTileEntity) ((TorcherinoTileEntity) tile).setCustomName(stack.getDisplayName());
		}
	}

	@Override @ParametersAreNonnullByDefault public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (world.isRemote || hand == EnumHand.OFF_HAND) return true;
		TileEntity tile = world.getTileEntity(pos);
		if (!(tile instanceof TorcherinoTileEntity)) return true;
		NetworkHooks.openGui((EntityPlayerMP) player, (TorcherinoTileEntity) tile, pos);
		return true;
	}

	@OnlyIn(Dist.CLIENT) @Override public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		tooltip.add(new TextComponentString(""));
		tooltip.add(new TextComponentTranslation("tooltip.torcherino.usageinformation").applyTextStyle(TextFormatting.GRAY));
		tooltip.add(new TextComponentString("").appendSibling(new TextComponentKeybind("key.use").applyTextStyle(TextFormatting.GOLD)).appendSibling(new TextComponentString(" ")).appendSibling(new TextComponentTranslation("tooltip.torcherino.change_speed")));
		tooltip.add(new TextComponentString("").appendSibling(new TextComponentKeybind("key.torcherino.modifier").applyTextStyle(TextFormatting.GOLD)).appendSibling(new TextComponentString(" + ")).appendSibling(new TextComponentKeybind("key.use").applyTextStyle(TextFormatting.GOLD)).appendSibling(new TextComponentString(" ")).appendSibling(new TextComponentTranslation("tooltip.torcherino.change_area")));
	}
}
