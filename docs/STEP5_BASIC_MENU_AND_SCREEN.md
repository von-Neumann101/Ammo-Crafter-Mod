# Step 5: Basic Menu and Screen

## 1. Goal

Implement the minimum GUI/Menu skeleton for `yourammocrafter:ammo_crafter`:

- right-clicking the block opens a server-backed menu;
- the menu exposes 9 material input slots and 9 output slots from `AmmoCrafterBlockEntity`;
- the menu also shows the player inventory and hotbar;
- output slots reject manual insertion;
- shift-click moves items only between valid slot groups.

No ghost template slot, template interaction, crafting, material consumption, redstone behavior, hopper capability, or TaCZ API dependency is implemented in this step.

## 2. Files Added or Modified

Added:

- `src/main/java/com/yourammocrafter/registry/ModMenus.java`
- `src/main/java/com/yourammocrafter/menu/AmmoCrafterMenu.java`
- `src/main/java/com/yourammocrafter/client/ClientModEvents.java`
- `src/main/java/com/yourammocrafter/client/screen/AmmoCrafterScreen.java`
- `docs/STEP5_BASIC_MENU_AND_SCREEN.md`

Modified:

- `src/main/java/com/yourammocrafter/YourAmmoCrafterMod.java`
- `src/main/java/com/yourammocrafter/block/AmmoCrafterBlock.java`
- `src/main/java/com/yourammocrafter/blockentity/AmmoCrafterBlockEntity.java`
- `src/main/resources/assets/yourammocrafter/lang/en_us.json`
- `src/main/resources/assets/yourammocrafter/lang/zh_cn.json`

## 3. Registered MenuType

MenuType registry id:

```text
yourammocrafter:ammo_crafter
```

Implementation:

- `ModMenus.MENU_TYPES` uses `DeferredRegister.create(BuiltInRegistries.MENU, YourAmmoCrafterMod.MOD_ID)`.
- `ModMenus.AMMO_CRAFTER` creates a `MenuType<AmmoCrafterMenu>` with NeoForge `IContainerFactory`.
- The client constructor reads the block position from the network buffer with `buffer.readBlockPos()`.
- `YourAmmoCrafterMod` registers `ModMenus.MENU_TYPES` on the mod event bus.

## 4. Slot Layout

Menu slot index ranges:

- input slots: 9 slots, indices `0..8`, backed by `AmmoCrafterBlockEntity#getInputItems()`;
- output slots: 9 slots, indices `9..17`, backed by `AmmoCrafterBlockEntity#getOutputItems()`;
- player inventory: 27 slots, indices `18..44`;
- hotbar: 9 slots, indices `45..53`.

Screen layout:

- input slots are displayed as a 3 x 3 grid;
- output slots are displayed as a 3 x 3 grid;
- player inventory is displayed as the normal 3 x 9 grid;
- hotbar is displayed below the player inventory.

Output slots use a custom `SlotItemHandler` subclass with `mayPlace(ItemStack)` returning `false`, so players can take from output slots but cannot manually place items into them.

## 5. Shift-click Behavior

`AmmoCrafterMenu#quickMoveStack` implements conservative movement:

- from block slots `0..17` to player inventory/hotbar `18..53`;
- from player inventory/hotbar `18..53` to input slots `0..8`;
- never moves player items into output slots `9..17`;
- invalid slot indices return `ItemStack.EMPTY`;
- if no item count changes, the method returns `ItemStack.EMPTY`.

This step does not generate output items. It only preserves and moves already stored stacks.

## 6. Client Screen Registration

Client screen registration is in `ClientModEvents`:

```java
@EventBusSubscriber(modid = YourAmmoCrafterMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
```

The class registers:

```java
event.register(ModMenus.AMMO_CRAFTER.get(), AmmoCrafterScreen::new);
```

Because the event subscriber is restricted to `Dist.CLIENT`, the dedicated server does not need to load `AmmoCrafterScreen` or Minecraft client GUI classes.

## 7. What Is Not Implemented Yet

- no ghost template slot;
- no template interaction;
- no template display;
- no redstone crafting;
- no hopper capability;
- no material consumption;
- no output ammo generation;
- no TaCZ API dependency;
- no TaCZ mandatory dependency.

## 8. Build Result

Build command used from `D:\work\minecraft-mods\YourAmmoCrafterMod`:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat build
```

Result:

```text
BUILD SUCCESSFUL
```

The only compile fix needed during this step was using the 1.21.1 package for `ContainerLevelAccess`:

```java
net.minecraft.world.inventory.ContainerLevelAccess
```
