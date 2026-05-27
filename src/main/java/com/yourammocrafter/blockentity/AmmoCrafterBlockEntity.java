package com.yourammocrafter.blockentity;

import com.yourammocrafter.menu.AmmoCrafterMenu;
import com.yourammocrafter.registry.ModBlockEntities;
import com.yourammocrafter.tacz.AmmoTemplateData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AmmoCrafterBlockEntity extends BlockEntity implements MenuProvider {
    public static final int INPUT_SLOT_COUNT = 9;
    public static final int OUTPUT_SLOT_COUNT = 9;

    private static final Component TITLE = Component.translatable("container.yourammocrafter.ammo_crafter");
    private static final String INPUT_ITEMS_TAG = "InputItems";
    private static final String OUTPUT_ITEMS_TAG = "OutputItems";

    private Optional<AmmoTemplateData> ammoTemplate = Optional.empty();

    private final ItemStackHandler inputItems = new ItemStackHandler(INPUT_SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            AmmoCrafterBlockEntity.this.setChanged();
        }
    };

    private final ItemStackHandler outputItems = new ItemStackHandler(OUTPUT_SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            AmmoCrafterBlockEntity.this.setChanged();
        }
    };

    public AmmoCrafterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.AMMO_CRAFTER.get(), pos, blockState);
    }

    public Optional<AmmoTemplateData> getAmmoTemplate() {
        return this.ammoTemplate;
    }

    public void setAmmoTemplate(@Nullable AmmoTemplateData ammoTemplate) {
        this.ammoTemplate = Optional.ofNullable(ammoTemplate);
        this.setChanged();
    }

    public void clearAmmoTemplate() {
        this.ammoTemplate = Optional.empty();
        this.setChanged();
    }

    public ItemStackHandler getInputItems() {
        return this.inputItems;
    }

    public ItemStackHandler getOutputItems() {
        return this.outputItems;
    }

    @Override
    public Component getDisplayName() {
        return TITLE;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new AmmoCrafterMenu(containerId, playerInventory, this);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);

        this.ammoTemplate = Optional.empty();
        if (tag.contains(AmmoTemplateData.BLOCK_ENTITY_TAG, Tag.TAG_COMPOUND)) {
            this.ammoTemplate = AmmoTemplateData.load(tag.getCompound(AmmoTemplateData.BLOCK_ENTITY_TAG));
        }

        CompoundTag inputTag = tag.contains(INPUT_ITEMS_TAG, Tag.TAG_COMPOUND)
                ? tag.getCompound(INPUT_ITEMS_TAG)
                : emptyInventoryTag(INPUT_SLOT_COUNT);
        this.inputItems.deserializeNBT(provider, inputTag);

        CompoundTag outputTag = tag.contains(OUTPUT_ITEMS_TAG, Tag.TAG_COMPOUND)
                ? tag.getCompound(OUTPUT_ITEMS_TAG)
                : emptyInventoryTag(OUTPUT_SLOT_COUNT);
        this.outputItems.deserializeNBT(provider, outputTag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);

        this.ammoTemplate.ifPresent(template ->
                tag.put(AmmoTemplateData.BLOCK_ENTITY_TAG, template.save())
        );
        tag.put(INPUT_ITEMS_TAG, this.inputItems.serializeNBT(provider));
        tag.put(OUTPUT_ITEMS_TAG, this.outputItems.serializeNBT(provider));
    }

    private static CompoundTag emptyInventoryTag(int size) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Size", size);
        tag.put("Items", new ListTag());
        return tag;
    }
}
