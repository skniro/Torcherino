package torcherino.Blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCarvedPumpkin;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.commons.lang3.StringUtils;
import torcherino.Blocks.Tiles.TileEntityTorcherino;
import torcherino.Utils;
import javax.annotation.Nullable;
import java.util.Random;

public class BlockLanterino extends BlockCarvedPumpkin
{
	private int MAX_SPEED;
	BlockLanterino(int speed)
	{
		super(Block.Properties.create(Material.GROUND, MaterialColor.ADOBE).hardnessAndResistance(1.0F).sound(SoundType.WOOD).lightValue(15));
		MAX_SPEED = speed;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) { return true; }

	public TileEntity createTileEntity(IBlockState state, IBlockReader world) { return new TileEntityTorcherino(MAX_SPEED); }

	@Override
	public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack)
	{
		player.addStat(StatList.BLOCK_MINED.get(this));
		player.addExhaustion(0.005F);
		if(Utils.keyStates.getOrDefault(player, false))
		{
			Item item = GameRegistry.findRegistry(Item.class).getValue(Utils.getId(this.getRegistryName().getPath().replace("lanterino", "torcherino")));
			if(item != null)
			{
				spawnAsEntity(world, pos, new ItemStack(Blocks.CARVED_PUMPKIN.asItem()));
				spawnAsEntity(world, pos, new ItemStack(item));
			}
			else
				spawnAsEntity(world, pos, new ItemStack(this.asItem()));
		}
		else
			spawnAsEntity(world, pos, new ItemStack(this.asItem()));

	}

	@Override
	public void neighborChanged(IBlockState selfState, World world, BlockPos selfPos, Block neighborBlock, BlockPos neighborPos)
	{
		if (world.isRemote) return;
		TileEntity tileEntity = world.getTileEntity(selfPos);
		if (tileEntity == null) return;
		((TileEntityTorcherino) tileEntity).setPoweredByRedstone(world.isBlockPowered(selfPos));
	}

	@Override
	public void onBlockAdded(IBlockState state, World world, BlockPos blockPos, IBlockState oldState) { neighborChanged(state, world, blockPos, null, null); }

	@Override
	public EnumPushReaction getPushReaction(IBlockState state) { return EnumPushReaction.IGNORE; }

	@Override
	public void tick(IBlockState state, World world, BlockPos pos, Random random)
	{
		TileEntity tileEntity = world.getTileEntity(pos);
		if(tileEntity instanceof ITickable) ((ITickable) tileEntity).tick();
	}

	@Override
	public void onReplaced(IBlockState state, World world, BlockPos pos, IBlockState state1, boolean b)
	{
		TileEntity tileEntity = world.getTileEntity(pos);
		if(tileEntity != null) tileEntity.remove();
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack)
	{
		if(world.isRemote) return;
		if(!Utils.logPlacement) return;
		String prefix = "Something";
		if(placer != null) prefix = placer.getDisplayName().getString() + "(" + placer.getCachedUniqueIdString() + ")";
		Utils.LOGGER.info("[Torcherino] {} placed a {} at {} {} {}.", prefix, StringUtils.capitalize(getTranslationKey().replace("block.torcherino.", "").replace("_", " ")), pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (world.isRemote) return true;
		if (hand == EnumHand.OFF_HAND) return true;
		TileEntity tile = world.getTileEntity(pos);
		if (!(tile instanceof TileEntityTorcherino)) return true;
		TileEntityTorcherino torch = (TileEntityTorcherino) tile;
		torch.changeMode(Utils.keyStates.getOrDefault(player, false));
		player.sendStatusMessage(torch.getDescription(), true);
		return true;
	}

}
