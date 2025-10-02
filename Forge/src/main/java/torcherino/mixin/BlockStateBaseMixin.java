package torcherino.mixin;
/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin{  /*extends StateHolder<Block, BlockState> implements LanterinoOxidizableRegistry.RandomTickCacheRefresher {
    @Shadow
    private boolean isRandomlyTicking;

    protected BlockStateBaseMixin(Block p_61117_, Reference2ObjectArrayMap<Property<?>, Comparable<?>> p_326342_, MapCodec<BlockState> p_61119_) {
        super(p_61117_, p_326342_, p_61119_);
    }

    @Shadow
    protected abstract BlockState asState();

    @Override
    public void torcherino$refreshRandomTickCache() {
        this.isRandomlyTicking = ((AbstractBlockAccessor) this.owner).callIsRandomlyTicking(this.asState());
    }*/
}
