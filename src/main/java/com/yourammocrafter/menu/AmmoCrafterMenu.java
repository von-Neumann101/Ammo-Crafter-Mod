package com.yourammocrafter.menu;

import com.yourammocrafter.blockentity.AmmoCrafterBlockEntity;
import com.yourammocrafter.registry.ModBlocks;
import com.yourammocrafter.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.Objects;

public class AmmoCrafterMenu extends AbstractContainerMenu {
    public static final int INPUT_SLOT_START = 0;
    public static final int INPUT_SLOT_COUNT = AmmoCrafterBlockEntity.INPUT_SLOT_COUNT;
    public static final int OUTPUT_SLOT_START = INPUT_SLOT_START + INPUT_SLOT_COUNT;
    public static final int OUTPUT_SLOT_COUNT = AmmoCrafterBlockEntity.OUTPUT_SLOT_COUNT;
    public static final int PLAYER_INVENTORY_SLOT_START = OUTPUT_SLOT_START + OUTPUT_SLOT_COUNT;
    public static final int PLAYER_INVENTORY_SLOT_COUNT = 27;
    public static final int HOTBAR_SLOT_START = PLAYER_INVENTORY_SLOT_START + PLAYER_INVENTORY_SLOT_COUNT;
    public static final int HOTBAR_SLOT_COUNT = 9;
    public static final int TOTAL_SLOT_COUNT = HOTBAR_SLOT_START + HOTBAR_SLOT_COUNT;

    private static final int INPUT_SLOTS_X = 26;
    private static final int INPUT_SLOTS_Y = 18;
    private static final int OUTPUT_SLOTS_X = 116;
    private static final int OUTPUT_SLOTS_Y = 18;
    private static final int PLAYER_INVENTORY_X = 8;
    private static final int PLAYER_INVENTORY_Y = 102;
    private static final int HOTBAR_Y = 160;

    private final AmmoCrafterBlockEntity blockEntity;
    private final ContainerLevelAccess access;

    public AmmoCrafterMenu(int containerId, Inventory playerInventory, BlockPos pos) {
        this(containerId, playerInventory, getBlockEntity(playerInventory, pos));
    }

    public AmmoCrafterMenu(int containerId, Inventory playerInventory, AmmoCrafterBlockEntity blockEntity) {
        this(containerId, playerInventory, blockEntity, ContainerLevelAccess.create(
                Objects.requireNonNull(blockEntity.getLevel()), blockEntity.getBlockPos()));
    }

    private AmmoCrafterMenu(
            int containerId,
            Inventory playerInventory,
            AmmoCrafterBlockEntity blockEntity,
            ContainerLevelAccess access
    ) {
        super(ModMenus.AMMO_CRAFTER.get(), containerId);
        this.blockEntity = blockEntity;
        this.access = access;

        addInputSlots(blockEntity.getInputItems());
        addOutputSlots(blockEntity.getOutputItems());
        addPlayerInventorySlots(playerInventory);
        addHotbarSlots(playerInventory);
    }

    public AmmoCrafterBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= this.slots.size()) {
            return ItemStack.EMPTY;
        }

        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stackInSlot = slot.getItem();
        ItemStack originalStack = stackInSlot.copy();

        if (index >= INPUT_SLOT_START && index < PLAYER_INVENTORY_SLOT_START) {
            if (!this.moveItemStackTo(stackInSlot, PLAYER_INVENTORY_SLOT_START, TOTAL_SLOT_COUNT, true)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= PLAYER_INVENTORY_SLOT_START && index < TOTAL_SLOT_COUNT) {
            if (!this.moveItemStackTo(stackInSlot, INPUT_SLOT_START, OUTPUT_SLOT_START, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (stackInSlot.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (stackInSlot.getCount() == originalStack.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, stackInSlot);
        return originalStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.AMMO_CRAFTER.get());
    }

    private void addInputSlots(ItemStackHandler inputItems) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                int slotIndex = column + row * 3;
                this.addSlot(new SlotItemHandler(
                        inputItems,
                        slotIndex,
                        INPUT_SLOTS_X + column * 18,
                        INPUT_SLOTS_Y + row * 18
                ));
            }
        }
    }

    private void addOutputSlots(ItemStackHandler outputItems) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                int slotIndex = column + row * 3;
                this.addSlot(new OutputSlot(
                        outputItems,
                        slotIndex,
                        OUTPUT_SLOTS_X + column * 18,
                        OUTPUT_SLOTS_Y + row * 18
                ));
            }
        }
    }

    private void addPlayerInventorySlots(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                int slotIndex = column + row * 9 + 9;
                this.addSlot(new Slot(
                        playerInventory,
                        slotIndex,
                        PLAYER_INVENTORY_X + column * 18,
                        PLAYER_INVENTORY_Y + row * 18
                ));
            }
        }
    }

    private void addHotbarSlots(Inventory playerInventory) {
        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(
                    playerInventory,
                    column,
                    PLAYER_INVENTORY_X + column * 18,
                    HOTBAR_Y
            ));
        }
    }

    private static AmmoCrafterBlockEntity getBlockEntity(Inventory playerInventory, BlockPos pos) {
        BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(pos);
        if (blockEntity instanceof AmmoCrafterBlockEntity ammoCrafterBlockEntity) {
            return ammoCrafterBlockEntity;
        }
        throw new IllegalStateException("Expected Ammo Crafter BlockEntity at " + pos);
    }

    private static final class OutputSlot extends SlotItemHandler {
        private OutputSlot(ItemStackHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }
}
