package com.yourammocrafter.blockentity;

import com.mojang.logging.LogUtils;
import com.yourammocrafter.crafting.AmmoCraftingRule;
import com.yourammocrafter.crafting.AmmoCraftingRules;
import com.yourammocrafter.crafting.CountedIngredient;
import com.yourammocrafter.menu.AmmoCrafterMenu;
import com.yourammocrafter.registry.ModBlockEntities;
import com.yourammocrafter.tacz.AmmoTemplateData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.StringJoiner;

public class AmmoCrafterBlockEntity extends BlockEntity implements MenuProvider {
    public static final int INPUT_SLOT_COUNT = 9;
    public static final int OUTPUT_SLOT_COUNT = 9;

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int CRAFT_INTERVAL_TICKS = 10;
    private static final Component TITLE = Component.translatable("container.yourammocrafter.ammo_crafter");
    private static final String INPUT_ITEMS_TAG = "InputItems";
    private static final String OUTPUT_ITEMS_TAG = "OutputItems";

    private Optional<AmmoTemplateData> ammoTemplate = Optional.empty();
    private int craftCooldown;

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
    private final IItemHandler inputAutomationHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return AmmoCrafterBlockEntity.this.inputItems.getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return AmmoCrafterBlockEntity.this.inputItems.getStackInSlot(slot);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return AmmoCrafterBlockEntity.this.inputItems.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return AmmoCrafterBlockEntity.this.inputItems.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return AmmoCrafterBlockEntity.this.inputItems.isItemValid(slot, stack);
        }
    };
    private final IItemHandler outputAutomationHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return AmmoCrafterBlockEntity.this.outputItems.getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return AmmoCrafterBlockEntity.this.outputItems.getStackInSlot(slot);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return AmmoCrafterBlockEntity.this.outputItems.extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return AmmoCrafterBlockEntity.this.outputItems.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }
    };

    public AmmoCrafterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.AMMO_CRAFTER.get(), pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AmmoCrafterBlockEntity blockEntity) {
        if (level.isClientSide) {
            return;
        }

        if (!level.hasNeighborSignal(pos)) {
            blockEntity.craftCooldown = 0;
            return;
        }

        blockEntity.craftCooldown++;
        if (blockEntity.craftCooldown >= CRAFT_INTERVAL_TICKS) {
            blockEntity.craftCooldown = 0;
            blockEntity.tryCraftOnce();
        }
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

    @Nullable
    public IItemHandler getAutomationItemHandler(@Nullable Direction side) {
        if (side == null) {
            return this.inputAutomationHandler;
        }
        return side == Direction.DOWN ? this.outputAutomationHandler : this.inputAutomationHandler;
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

    private boolean tryCraftOnce() {
        AmmoTemplateData template = this.ammoTemplate.orElse(null);
        if (template == null) {
            LOGGER.info(
                    "Ammo crafter craft attempt at {} failed=no_template loadedRuleCount={} loadedAmmoIds={}",
                    this.worldPosition,
                    AmmoCraftingRules.count(),
                    AmmoCraftingRules.ammoIds()
            );
            return false;
        }

        ResourceLocation ammoId = template.ammoId();
        AmmoCraftingRule rule = AmmoCraftingRules.find(ammoId).orElse(null);
        if (rule == null) {
            logCraftAttempt(ammoId, false, 0, true, false, false, "no_rule");
            return false;
        }

        ItemStack output = template.createStack(rule.outputCount());
        boolean outputEmpty = output.isEmpty();
        boolean ingredientsSufficient = hasIngredients(rule);
        boolean outputCanFit = !outputEmpty && canInsertAllOutput(output);

        if (outputEmpty) {
            logCraftAttempt(ammoId, true, rule.outputCount(), true, ingredientsSufficient, false, "output_empty");
            return false;
        }

        if (!ingredientsSufficient) {
            LOGGER.info(
                    "Ammo crafter missing ingredients at {} missing={} inputSlots={}",
                    this.worldPosition,
                    describeMissingIngredients(rule),
                    describeInputContents()
            );
            logCraftAttempt(ammoId, true, rule.outputCount(), false, false, outputCanFit, "missing_ingredients");
            return false;
        }

        if (!outputCanFit) {
            logCraftAttempt(ammoId, true, rule.outputCount(), false, true, false, "output_full");
            return false;
        }

        logCraftAttempt(ammoId, true, rule.outputCount(), false, true, true, "success");
        consumeIngredients(rule);
        insertOutput(output);
        this.setChanged();
        return true;
    }

    private void logCraftAttempt(
            ResourceLocation ammoId,
            boolean foundRule,
            int outputCount,
            boolean outputEmpty,
            boolean ingredientsSufficient,
            boolean outputCanFit,
            String result
    ) {
        LOGGER.info(
                "Ammo crafter craft attempt at {} ammoId={} loadedRuleCount={} loadedAmmoIds={} foundRule={} outputCount={} outputEmpty={} ingredientsSufficient={} outputCanFit={} result={}",
                this.worldPosition,
                ammoId,
                AmmoCraftingRules.count(),
                AmmoCraftingRules.ammoIds(),
                foundRule,
                outputCount,
                outputEmpty,
                ingredientsSufficient,
                outputCanFit,
                result
        );
    }

    private boolean hasIngredients(AmmoCraftingRule rule) {
        for (CountedIngredient ingredient : rule.ingredients()) {
            int found = 0;
            for (int slot = 0; slot < this.inputItems.getSlots(); slot++) {
                ItemStack stack = this.inputItems.getStackInSlot(slot);
                if (ingredient.ingredient().test(stack)) {
                    found += stack.getCount();
                    if (found >= ingredient.count()) {
                        break;
                    }
                }
            }
            if (found < ingredient.count()) {
                return false;
            }
        }
        return true;
    }

    private String describeMissingIngredients(AmmoCraftingRule rule) {
        StringJoiner missing = new StringJoiner(", ", "[", "]");
        for (CountedIngredient ingredient : rule.ingredients()) {
            int found = countIngredient(ingredient);
            if (found < ingredient.count()) {
                missing.add(describeIngredient(ingredient) + " found=" + found + " required=" + ingredient.count());
            }
        }
        return missing.toString();
    }

    private int countIngredient(CountedIngredient ingredient) {
        int found = 0;
        for (int slot = 0; slot < this.inputItems.getSlots(); slot++) {
            ItemStack stack = this.inputItems.getStackInSlot(slot);
            if (ingredient.ingredient().test(stack)) {
                found += stack.getCount();
            }
        }
        return found;
    }

    private static String describeIngredient(CountedIngredient ingredient) {
        StringJoiner items = new StringJoiner("|");
        for (ItemStack stack : ingredient.ingredient().getItems()) {
            items.add(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
        }
        return items.toString();
    }

    private String describeInputContents() {
        StringJoiner contents = new StringJoiner(", ", "[", "]");
        for (int slot = 0; slot < this.inputItems.getSlots(); slot++) {
            ItemStack stack = this.inputItems.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                contents.add(slot + "=" + BuiltInRegistries.ITEM.getKey(stack.getItem()) + "x" + stack.getCount());
            }
        }
        return contents.toString();
    }

    private void consumeIngredients(AmmoCraftingRule rule) {
        for (CountedIngredient ingredient : rule.ingredients()) {
            int remaining = ingredient.count();
            for (int slot = 0; slot < this.inputItems.getSlots() && remaining > 0; slot++) {
                ItemStack stack = this.inputItems.getStackInSlot(slot);
                if (ingredient.ingredient().test(stack)) {
                    int toExtract = Math.min(remaining, stack.getCount());
                    ItemStack extracted = this.inputItems.extractItem(slot, toExtract, false);
                    remaining -= extracted.getCount();
                }
            }
        }
    }

    private boolean canInsertAllOutput(ItemStack output) {
        ItemStack remaining = output.copy();
        for (int slot = 0; slot < this.outputItems.getSlots(); slot++) {
            remaining = this.outputItems.insertItem(slot, remaining, true);
            if (remaining.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void insertOutput(ItemStack output) {
        ItemStack remaining = output.copy();
        for (int slot = 0; slot < this.outputItems.getSlots() && !remaining.isEmpty(); slot++) {
            remaining = this.outputItems.insertItem(slot, remaining, false);
        }
    }

    private static CompoundTag emptyInventoryTag(int size) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Size", size);
        tag.put("Items", new ListTag());
        return tag;
    }
}
