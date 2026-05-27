# Step 4: Block and BlockEntity Storage

## 1. Goal

Register the minimal Ammo Crafter block, BlockItem, and BlockEntityType, then add a BlockEntity storage layout for ammo template data, material input slots, and output slots.

This step only adds registration, block placement support, BlockEntity storage, and NBT persistence.

## 2. Files Added or Modified

Modified:

- `src/main/java/com/yourammocrafter/YourAmmoCrafterMod.java`

Added:

- `src/main/java/com/yourammocrafter/registry/ModBlocks.java`
- `src/main/java/com/yourammocrafter/registry/ModItems.java`
- `src/main/java/com/yourammocrafter/registry/ModBlockEntities.java`
- `src/main/java/com/yourammocrafter/block/AmmoCrafterBlock.java`
- `src/main/java/com/yourammocrafter/blockentity/AmmoCrafterBlockEntity.java`
- `src/main/resources/assets/yourammocrafter/blockstates/ammo_crafter.json`
- `src/main/resources/assets/yourammocrafter/models/block/ammo_crafter.json`
- `src/main/resources/assets/yourammocrafter/models/item/ammo_crafter.json`
- `src/main/resources/assets/yourammocrafter/lang/en_us.json`
- `src/main/resources/assets/yourammocrafter/lang/zh_cn.json`
- `docs/STEP4_BLOCK_AND_BLOCKENTITY_STORAGE.md`

## 3. Registered Objects

- Block id: `yourammocrafter:ammo_crafter`
- Block item id: `yourammocrafter:ammo_crafter`
- Block entity type id: `yourammocrafter:ammo_crafter`

Registration uses NeoForge `DeferredRegister`:

- `DeferredRegister.createBlocks("yourammocrafter")`
- `DeferredRegister.createItems("yourammocrafter")`
- `DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, "yourammocrafter")`

The main mod class registers all three deferred registers on the mod event bus.

## 4. BlockEntity Data Layout

`AmmoCrafterBlockEntity` stores:

- `ammoTemplate`: `Optional<AmmoTemplateData>`
  - Empty when no template is configured.
  - Setter and clear method call `setChanged()`.
- `inputItems`: `ItemStackHandler`
  - Slot count: `9`.
  - Contents changes call `setChanged()`.
- `outputItems`: `ItemStackHandler`
  - Slot count: `9`.
  - Contents changes call `setChanged()`.

The output slots are only persisted in this step. No insertion/extraction rules are implemented yet.

## 5. NBT Format

Actual saved root keys:

```snbt
{
  AmmoTemplate: {
    AmmoItemId: "tacz:ammo",
    AmmoId: "tacz:762x39"
  },
  InputItems: {
    Size: 9,
    Items: [
      {
        Slot: 0,
        id: "minecraft:iron_ingot",
        count: 1
      }
    ]
  },
  OutputItems: {
    Size: 9,
    Items: [
      {
        Slot: 0,
        id: "tacz:ammo",
        count: 64,
        components: {
          "minecraft:custom_data": {
            AmmoId: "tacz:762x39"
          }
        }
      }
    ]
  }
}
```

Notes:

- `AmmoTemplate` is absent when no template is set.
- Missing `AmmoTemplate` loads as `Optional.empty()`.
- Missing `InputItems` or `OutputItems` loads as an empty 9-slot handler.
- Save/load uses the Minecraft 1.21.1 `HolderLookup.Provider` signatures:
  - `saveAdditional(CompoundTag tag, HolderLookup.Provider provider)`
  - `loadAdditional(CompoundTag tag, HolderLookup.Provider provider)`
- `ItemStackHandler` persistence uses:
  - `serializeNBT(provider)`
  - `deserializeNBT(provider, tag)`

## 6. What Is Not Implemented Yet

- No GUI.
- No menu.
- No screen.
- No ghost slot interaction.
- No redstone crafting.
- No hopper capability.
- No `RegisterCapabilitiesEvent`.
- No material consumption.
- No full crafting logic.
- No TaCZ API usage.
- No mandatory TaCZ dependency.

## 7. Build Result

Build command used:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat build
```

Result:

```text
BUILD SUCCESSFUL in 11s
```

The build compiled the new block, registry, BlockEntity, resource files, and existing ammo template utilities successfully.
