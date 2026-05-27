# Step 1: TaCZ Ammo ItemStack Research

## 1. Scope

This document only investigates the real TaCZ 1.21.1 NeoForge ammo `ItemStack` structure. It does not implement blocks, block entities, menus, screens, GUI, ghost slots, redstone, hopper logic, crafting logic, or Java source changes.

Sources checked:

- `D:\work\minecraft-mods\TACZ-1.21.1`
- `D:\work\minecraft-mods\TaCZPackUpgrader`
- `D:\work\minecraft-mods\YourAmmoCrafterMod`
- Gradle cache search: `C:\Users\ASUS\.gradle\caches`
- Workspace jar search: `D:\work\minecraft-mods`

Before this document was created, `YourAmmoCrafterMod` contained no project files. After this step it only contains this document. Searches for TaCZ jars in the workspace and Gradle cache did not return a matching jar, so the conclusions below are based on the local TaCZ source and TaCZPackUpgrader source, not on decompiled jar output.

Search terms used included: `IAmmo`, `AmmoItem`, `AmmoId`, `ammo_id`, `AmmoItemDataAccessor`, `DataComponent`, `DataComponents`, `CustomData`, `CUSTOM_DATA`, `getAmmoId`, `setAmmoId`, `tacz:ammo`, `DeferredRegister`, `ItemStack`, `CompoundTag`, `ResourceLocation`.

## 2. Ammo Base Item

TaCZ ammo is one base item registered as `tacz:ammo`.

Evidence:

- `D:\work\minecraft-mods\TACZ-1.21.1\src\main\java\com\tacz\guns\GunMod.java`
  - Class: `com.tacz.guns.GunMod`
  - Field: `MOD_ID`
  - Lines 20-22: `@Mod(GunMod.MOD_ID)` and `MOD_ID = "tacz"`.
- `D:\work\minecraft-mods\TACZ-1.21.1\src\main\java\com\tacz\guns\init\ModItems.java`
  - Class: `com.tacz.guns.init.ModItems`
  - Field: `ITEMS`
  - Line 17: `DeferredRegister.createItems(GunMod.MOD_ID)`.
  - Field: `AMMO`
  - Line 23: `ITEMS.register("ammo", AmmoItem::new)`.

The ammo item class is:

- Class name: `com.tacz.guns.item.AmmoItem`
- Source path: `D:\work\minecraft-mods\TACZ-1.21.1\src\main\java\com\tacz\guns\item\AmmoItem.java`
- Line 33: `public class AmmoItem extends Item implements AmmoItemDataAccessor`.

No second registered ammo item was found in `ModItems.java`. Related item `tacz:ammo_box` exists, but it is registered separately as `AMMO_BOX` on line 34 and uses `AmmoBoxItem`, not `AmmoItem`.

## 3. Ammo Type Storage

Different ammo types are stored in the item stack's Minecraft 1.21.1 data component `DataComponents.CUSTOM_DATA`, serialized as `minecraft:custom_data`.

The key name is exactly `AmmoId`.

Evidence:

- `D:\work\minecraft-mods\TACZ-1.21.1\src\main\java\com\tacz\guns\api\item\nbt\AmmoItemDataAccessor.java`
  - Class/interface: `com.tacz.guns.api.item.nbt.AmmoItemDataAccessor`
  - Line 20: `AmmoItemDataAccessor extends IAmmo`.
  - Line 21: `String AMMO_ID_TAG = "AmmoId"`.
  - Method: `getAmmoId(ItemStack ammo)`
  - Lines 25-31: reads `ammo.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag()`, checks `AmmoId` as `Tag.TAG_STRING`, parses it with `ResourceLocation.tryParse`, and returns `DefaultAssets.EMPTY_AMMO_ID` if absent/invalid.
  - Method: `setAmmoId(ItemStack ammo, @Nullable ResourceLocation ammoId)`
  - Lines 35-46: writes `AmmoId` into `DataComponents.CUSTOM_DATA` with `tag.putString(AMMO_ID_TAG, ammoId.toString())`.

The Java API returns the ammo id as a `ResourceLocation`, but the stored value inside custom data is a string.

Evidence:

- `D:\work\minecraft-mods\TACZ-1.21.1\src\main\java\com\tacz\guns\api\item\IAmmo.java`
  - Class/interface: `com.tacz.guns.api.item.IAmmo`
  - Method: `getAmmoId(ItemStack ammo)`
  - Line 29: return type is `ResourceLocation`.
  - Method: `setAmmoId(ItemStack ammo, @Nullable ResourceLocation ammoId)`
  - Line 34: input type is `ResourceLocation`.
- `AmmoItemDataAccessor.java`
  - Line 28: `ResourceLocation.tryParse(nbt.getString(AMMO_ID_TAG))`.
  - Line 38: `tag.putString(AMMO_ID_TAG, ammoId.toString())`.

TaCZ 1.21.1 does use Minecraft DataComponents for ammo custom data. It does not use old direct `ItemStack#getTag()` style in the ammo accessor. It can still obtain a `CompoundTag`, but via `CustomData.copyTag()` from `DataComponents.CUSTOM_DATA`.

Evidence:

- `AmmoItemDataAccessor.java`
  - Line 26: `CompoundTag nbt = ammo.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();`
  - Line 36: `ammo.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, data -> data.update(tag -> { ... }))`.

TaCZ also updates max stack size from ammo index data when the ammo id is set or after loading.

Evidence:

- `AmmoItemDataAccessor.java`
  - Lines 39-41: if item is `IAmmo`, TaCZ reads `CommonAmmoIndex::getStackSize` and sets `DataComponents.MAX_STACK_SIZE`.
- `D:\work\minecraft-mods\TACZ-1.21.1\src\main\java\com\tacz\guns\item\AmmoItem.java`
  - Method: `verifyComponentsAfterLoad`
  - Lines 39-42: reads ammo id and sets `DataComponents.MAX_STACK_SIZE`.
- `D:\work\minecraft-mods\TACZ-1.21.1\src\main\resources\assets\tacz\custom\tacz_default_gun\data\tacz\index\ammo\762x39.json`
  - Line 5: `"stack_size": 60`.
- `D:\work\minecraft-mods\TACZ-1.21.1\src\main\java\com\tacz\guns\resource\index\CommonAmmoIndex.java`
  - Lines 24 and 28-30: clamps and returns the stack size.

Related helper/API classes found:

- `IAmmo`
  - `D:\work\minecraft-mods\TACZ-1.21.1\src\main\java\com\tacz\guns\api\item\IAmmo.java`
  - Lines 13-20: `getIAmmoOrNull(@Nullable ItemStack stack)` checks `stack.getItem() instanceof IAmmo`.
  - Lines 29 and 34: `getAmmoId` / `setAmmoId`.
- `AmmoItemDataAccessor`
  - `D:\work\minecraft-mods\TACZ-1.21.1\src\main\java\com\tacz\guns\api\item\nbt\AmmoItemDataAccessor.java`
  - Lines 20-57: default implementation of ammo id storage and `isAmmoOfGun`.
- `AmmoItemBuilder`
  - `D:\work\minecraft-mods\TACZ-1.21.1\src\main\java\com\tacz\guns\api\item\builder\AmmoItemBuilder.java`
  - Lines 34-39: builds `new ItemStack(ModItems.AMMO.get(), count)` and calls `IAmmo.setAmmoId`.

## 4. Real ItemStack Data Example

Runtime structure produced by TaCZ code for `tacz:762x39` ammo with count `35`:

```snbt
{
  id: "tacz:ammo",
  count: 35,
  components: {
    "minecraft:custom_data": {
      AmmoId: "tacz:762x39"
    },
    "minecraft:max_stack_size": 60
  }
}
```

Why this example is source-backed:

- `D:\work\minecraft-mods\TACZ-1.21.1\src\main\resources\assets\tacz\custom\tacz_default_gun\data\tacz\recipe\ammo\762x39.json`
  - Lines 16-20: recipe result has `type: "ammo"`, `id: "tacz:762x39"`, `count: 35`.
- `D:\work\minecraft-mods\TACZ-1.21.1\src\main\java\com\tacz\guns\crafting\result\GunSmithTableResult.java`
  - Lines 45-50: result type `ammo` reads `id` and `count` into `RawGunTableResult`.
- `D:\work\minecraft-mods\TACZ-1.21.1\src\main\java\com\tacz\guns\crafting\result\RawGunTableResult.java`
  - Lines 53-56: `ammo` type dispatches to `getAmmoStack`.
  - Lines 100-101: `getAmmoStack` calls `AmmoItemBuilder.create().setCount(count).setId(id).build()`.
- `AmmoItemBuilder.java`
  - Lines 34-39: creates `ModItems.AMMO` stack and calls `IAmmo.setAmmoId`.
- `AmmoItemDataAccessor.java`
  - Lines 35-46: writes `AmmoId` to `DataComponents.CUSTOM_DATA`; lines 39-41 set `DataComponents.MAX_STACK_SIZE`.
- `762x39.json`
  - Line 5: stack size is `60`.

Serialized JSON authored in TaCZ assets commonly stores the custom data component as an SNBT string:

```json
{
  "id": "tacz:ammo",
  "components": {
    "minecraft:custom_data": "{AmmoId: \"tacz:762x39\"}"
  }
}
```

Evidence:

- `D:\work\minecraft-mods\TACZ-1.21.1\src\main\resources\assets\tacz\custom\tacz_default_gun\data\tacz\data\blocks\ammo_workbench.json`
  - Lines 10-14: icon item `id: "tacz:ammo"` with `minecraft:custom_data` containing `{AmmoId: "tacz:762x39"}`.
- Same file:
  - Lines 20-24: `tacz:9mm`.
  - Lines 30-34: `tacz:556x45`.
  - Lines 40-44: `tacz:50bmg`.
  - Lines 50-54: `tacz:rpg_rocket`.
  - Lines 60-64: `tacz:12g`.
- `D:\work\minecraft-mods\TACZ-1.21.1\src\main\resources\assets\tacz\custom\tacz_default_gun\data\tacz\tacz_loot_injectors\spawn_bonus_chest_taurus943.json`
  - Lines 27-34: loot entry `name: "tacz:ammo"` with `minecraft:custom_data` `{AmmoId:"tacz:22wmr"}`.
  - Lines 36-40: loot count range `38` to `45`.

TaCZPackUpgrader confirms the 1.21.1 migration target is `components.minecraft:custom_data`.

Evidence:

- `D:\work\minecraft-mods\TaCZPackUpgrader\src\main\kotlin\me\muksc\taczpackupgrader\Upgrader.kt`
  - Method: `upgradeLootInjector`
  - Lines 257-261: converts `minecraft:set_nbt` into `minecraft:set_components` and puts the old `tag` string under `minecraft:custom_data`.
  - Method: `upgradeItemStack`
  - Lines 352-360: converts old `item` to `id` and old `nbt` object to `components.minecraft:custom_data`.

## 5. Ammo Stack Identification Strategy

Options compared:

- Registry id check:
  - Check item registry id equals `tacz:ammo`.
  - Does not require TaCZ compile-time dependency if implemented with vanilla/NeoForge registry APIs and a `ResourceLocation`.
  - Server side usable.
  - Stable for this local source because `ModItems.AMMO` registers `"ammo"` under mod id `"tacz"`.
  - It identifies TaCZ ammo stacks, but does not validate that `AmmoId` is present or valid.
- `item instanceof IAmmo`:
  - TaCZ's own helper uses this.
  - Evidence: `IAmmo.java` lines 13-20 and `AmmoItemDataAccessor.java` lines 50-54.
  - Requires TaCZ API classes at compile time unless reflection/optional integration is used.
  - Server side usable.
  - Stable against registry id changes only if TaCZ keeps the API/interface contract.
- Item tag check:
  - No dedicated ammo item tag was found in local TaCZ resources.
  - `D:\work\minecraft-mods\TACZ-1.21.1\src\main\resources\data\minecraft\tags\item\dyeable.json` lines 1-5 only lists `tacz:ammo_box`.
  - Search path `src\main\resources` found no item tag containing `tacz:ammo`.
  - Not recommended.
- NBT/DataComponent check:
  - Check `DataComponents.CUSTOM_DATA` / `minecraft:custom_data` has string key `AmmoId`.
  - Evidence: `AmmoItemDataAccessor.java` lines 21 and 25-31.
  - Does not prove the base item is TaCZ ammo by itself; ammo boxes also use `AmmoId` in `AmmoBoxItemDataAccessor`.
  - Better used after confirming `tacz:ammo`.
- TaCZ API/helper:
  - `IAmmo.getIAmmoOrNull(stack)` is the local TaCZ helper.
  - Evidence: `IAmmo.java` lines 13-20.
  - Requires TaCZ compile-time dependency.
  - Server side usable.

Recommended minimum if TaCZ is a hard compile-time dependency:

```java
IAmmo ammo = IAmmo.getIAmmoOrNull(stack);
if (ammo != null) {
    ResourceLocation ammoId = ammo.getAmmoId(stack);
}
```

Recommended minimum if TaCZ is not a hard compile-time dependency:

1. Check the item registry id is exactly `tacz:ammo`.
2. Read `DataComponents.CUSTOM_DATA` as `CustomData`, copy it to `CompoundTag`, and read `AmmoId` only if it is a string.
3. Treat the string as a `ResourceLocation` string. Do not require TaCZ classes for this path.

This avoids a compile-time TaCZ dependency while still matching the local TaCZ 1.21.1 item structure. The main risk is that it depends on TaCZ keeping the registry id `tacz:ammo` and custom-data key `AmmoId`.

## 6. Minimal Data Needed to Reconstruct Ammo Later

Minimum data to reconstruct the same TaCZ ammo stack later:

- Base item registry id: `tacz:ammo`.
- Stack count.
- Ammo id string stored at `components.minecraft:custom_data.AmmoId`, for example `tacz:762x39`.

`minecraft:max_stack_size` is not minimal reconstruction data if TaCZ is loaded, because TaCZ recalculates it from ammo index data:

- `AmmoItemDataAccessor.java` lines 39-41 sets `DataComponents.MAX_STACK_SIZE` when `setAmmoId` is called.
- `AmmoItem.java` lines 39-42 sets `DataComponents.MAX_STACK_SIZE` after load.
- `CommonAmmoIndex.java` lines 24 and 28-30 reads stack size from ammo index data.

If reconstructing without calling TaCZ's `IAmmo.setAmmoId` or without letting `AmmoItem.verifyComponentsAfterLoad` run, preserving or recomputing `minecraft:max_stack_size` may be needed for exact runtime parity.

## 7. Risks / Unknowns

- Current `YourAmmoCrafterMod` is empty, so no project-local Gradle dependency or jar could be checked there.
- No TaCZ jar was found by local searches in `D:\work\minecraft-mods` or `C:\Users\ASUS\.gradle\caches`; no decompiled jar evidence was used.
- No dedicated item tag for ammo was found. Searched local TaCZ resources for item tags; only `data\minecraft\tags\item\dyeable.json` was found, and it contains `tacz:ammo_box`, not `tacz:ammo`.
- `minecraft:custom_data` appears in source JSON as an SNBT string in authored examples and as a JSON object in TaCZPackUpgrader's old-`nbt` conversion path. Runtime TaCZ ammo access uses `CustomData.copyTag()` and therefore treats it as a `CompoundTag`.
- `AmmoId` is a string in storage and `ResourceLocation` in the TaCZ Java API. Invalid or missing values return `DefaultAssets.EMPTY_AMMO_ID`.
- `AmmoId` alone is not unique to loose ammo; ammo boxes also use an `AmmoId` field through `AmmoBoxItemDataAccessor`. Use `tacz:ammo` registry id or `IAmmo` before interpreting a stack as loose ammo.
