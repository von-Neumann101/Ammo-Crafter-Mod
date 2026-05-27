# Step 2: Ammo Template Data Implementation

## 1. Goal

Implement a minimal data-layer utility for TaCZ loose ammo templates. The code extracts the base ammo item id and TaCZ `AmmoId` from a real ammo `ItemStack`, saves/loads only those two ids as NBT, and rebuilds an output ammo `ItemStack` later.

This step does not add blocks, BlockEntities, GUI, menus, screens, ghost slots, redstone, hopper behavior, material consumption, or full crafting logic.

## 2. Implemented Classes

- `com.yourammocrafter.tacz.AmmoTemplateData`
  - Immutable `record`.
  - Fields:
    - `ammoItemId`: `ResourceLocation`, expected to be `tacz:ammo`.
    - `ammoId`: `ResourceLocation`, copied from `DataComponents.CUSTOM_DATA` key `AmmoId`.
  - API:
    - `static Optional<AmmoTemplateData> fromStack(ItemStack stack)`
    - `ItemStack createStack(int count)`
    - `CompoundTag save()`
    - `static Optional<AmmoTemplateData> load(CompoundTag tag)`
    - `boolean isValid()`
- `com.yourammocrafter.tacz.AmmoStackFactory`
  - Creates an `ItemStack` from `AmmoTemplateData` and `count`.
  - Does not consume materials or insert into inventories.

Neither class imports or calls `com.tacz.guns.*`.

## 3. Data Format

`AmmoTemplateData.save()` returns a flat `CompoundTag`:

```snbt
{
  AmmoItemId: "tacz:ammo",
  AmmoId: "tacz:762x39"
}
```

A future BlockEntity can store it under an outer key:

```snbt
{
  AmmoTemplate: {
    AmmoItemId: "tacz:ammo",
    AmmoId: "tacz:762x39"
  }
}
```

The constant for the optional outer key is `AmmoTemplateData.BLOCK_ENTITY_TAG`.

## 4. Extraction from TaCZ Ammo Stack

`AmmoTemplateData.fromStack`:

1. Rejects `ItemStack.EMPTY`.
2. Reads the item registry id with `BuiltInRegistries.ITEM.getKey(stack.getItem())`.
3. Requires the registry id to equal `tacz:ammo`.
4. Reads `DataComponents.CUSTOM_DATA` as `CustomData`.
5. Copies it to `CompoundTag` with `CustomData.copyTag()`.
6. Requires string key `AmmoId`.
7. Parses `AmmoId` with `ResourceLocation.tryParse`.

Missing custom data, missing `AmmoId`, empty strings, invalid ids, and non-`tacz:ammo` items return `Optional.empty()`.

## 5. Reconstruction to Output Stack

`AmmoStackFactory.create`:

1. Rejects null templates, invalid templates, and non-positive counts.
2. Looks up `ammoItemId` in `BuiltInRegistries.ITEM`.
3. Returns `ItemStack.EMPTY` if the item resolves to `Items.AIR`.
4. Creates `new ItemStack(item, count)`.
5. Creates a new `CompoundTag`.
6. Writes `AmmoId` as `template.ammoId().toString()`.
7. Sets `DataComponents.CUSTOM_DATA` with `CustomData.of(customData)`.

The generated stack shape is:

```snbt
{
  id: "tacz:ammo",
  count: 64,
  components: {
    "minecraft:custom_data": {
      AmmoId: "tacz:762x39"
    }
  }
}
```

## 6. Limitations

- The template deliberately does not store the full `ItemStack`.
- It does not store count, display name, damage, enchantments, lore, custom model data, or other unrelated components.
- It does not call TaCZ API and does not require a mandatory TaCZ compile-time dependency.
- It assumes the Step 1 structure remains true: loose ammo item id is `tacz:ammo`, and ammo type is `minecraft:custom_data.AmmoId`.
- It does not validate that `ammoId` exists in TaCZ's loaded ammo index. It only validates that it is a legal `ResourceLocation`.

## 7. Manual Verification Steps

1. Obtain a real TaCZ loose ammo `ItemStack`, for example `tacz:ammo` with custom data `AmmoId: "tacz:762x39"`.
2. Call `AmmoTemplateData.fromStack(stack)` and confirm the result is present.
3. Confirm `template.ammoItemId()` is `tacz:ammo` and `template.ammoId()` matches the stack's `AmmoId`.
4. Save with `template.save()`, then load with `AmmoTemplateData.load(savedTag)`.
5. Confirm the loaded template has the same `ammoId`.
6. Call `loadedTemplate.createStack(64)`.
7. Confirm the generated stack item registry id is `tacz:ammo`.
8. Confirm the generated stack has `DataComponents.CUSTOM_DATA`, and its copied `CompoundTag` has string key `AmmoId` equal to the template ammo id.
