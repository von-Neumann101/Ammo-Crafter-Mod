# Step 3: Project Scaffold and Compile Check

## 1. Goal

Make `YourAmmoCrafterMod` a minimal compilable Minecraft 1.21.1 NeoForge mod project and verify that the Step 2 ammo template utility classes compile.

This step does not implement blocks, BlockEntities, items, menus, screens, GUI, ghost slots, redstone, hopper logic, material consumption, or crafting logic.

## 2. Files Added or Modified

Added project scaffold:

- `settings.gradle`
- `build.gradle`
- `gradle.properties`
- `gradlew`
- `gradlew.bat`
- `gradle/wrapper/gradle-wrapper.jar`
- `gradle/wrapper/gradle-wrapper.properties`
- `src/main/java/com/yourammocrafter/YourAmmoCrafterMod.java`
- `src/main/resources/META-INF/neoforge.mods.toml`
- `src/main/resources/pack.mcmeta`

Added this documentation:

- `docs/STEP3_PROJECT_SCAFFOLD_AND_COMPILE.md`

Existing Step 2 classes were kept:

- `src/main/java/com/yourammocrafter/tacz/AmmoTemplateData.java`
- `src/main/java/com/yourammocrafter/tacz/AmmoStackFactory.java`

## 3. NeoForge / Minecraft Versions

The scaffold uses the same core versions as the local TaCZ 1.21.1 source project:

- Minecraft: `1.21.1`
- NeoForge: `21.1.93`
- ModDevGradle: `2.0.74`
- Gradle wrapper: `8.12`
- Java toolchain / compile target: `21`

The local default `java` command points to Java 24, so the successful build was run with `JAVA_HOME` temporarily set to `C:\Program Files\Java\jdk-21`.

## 4. Mod ID and Package

- Mod id: `yourammocrafter`
- Main package: `com.yourammocrafter`
- Main mod class: `com.yourammocrafter.YourAmmoCrafterMod`
- TaCZ utility package: `com.yourammocrafter.tacz`

The main mod class only declares the mod id and constructor. It registers no blocks, items, menus, screens, or gameplay systems.

## 5. AmmoTemplateData Compile Fixes

No Step 3 source changes were required for `AmmoTemplateData` or `AmmoStackFactory`.

The following Step 2 API usage compiled successfully against Minecraft 1.21.1 NeoForge mappings:

- `ResourceLocation.fromNamespaceAndPath("tacz", "ammo")`
- `ResourceLocation.tryParse(value)`
- `BuiltInRegistries.ITEM.getKey(stack.getItem())`
- `BuiltInRegistries.ITEM.get(template.ammoItemId())`
- `DataComponents.CUSTOM_DATA`
- `CustomData.copyTag()`
- `CustomData.of(CompoundTag)`
- `CompoundTag.putString(...)`
- `CompoundTag.getString(...)`
- `ItemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))`
- `ItemStack.EMPTY`
- `Items.AIR`

No `com.tacz.guns.*` API was imported or called.

## 6. Build Command Used

First attempt:

```powershell
gradle build
```

Result: failed because `gradle` was not installed on `PATH`.

Wrapper setup:

- Copied `gradlew`, `gradlew.bat`, and `gradle/wrapper/*` from the local TaCZ 1.21.1 project.

Second attempt:

```powershell
.\gradlew.bat build
```

Result: failed before source compilation because the default Java runtime was Java 24:

```text
Unsupported class file major version 68
```

Successful command:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat build
```

## 7. Build Result

Build succeeded.

Relevant Gradle tasks completed:

- `compileJava`
- `processResources`
- `classes`
- `jar`
- `assemble`
- `build`

The build output ended with:

```text
BUILD SUCCESSFUL
```

Gradle also printed `Cannot inject duplicate file mcp/client/Start.class` after the successful task list. This did not fail the build.

## 8. Remaining Issues

- The machine's default `java` command is Java 24. Use Java 21 for this project, or configure `JAVA_HOME`/Gradle JVM accordingly.
- No unit test framework was added.
- The build verifies compilation only; it does not runtime-test TaCZ ammo stack extraction or reconstruction.
- The project still has no TaCZ mandatory dependency and no direct TaCZ API usage.
