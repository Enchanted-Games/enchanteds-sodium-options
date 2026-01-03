package games.enchanted.enchanteds_sodium_options.common.mixin.accessor.sodium;

import net.caffeinemc.mods.sodium.client.config.structure.Option;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Option.class)
public interface OptionAccessor {
    @Accessor("id")
    public Identifier enchanteds_sodium_options$getId();
}
