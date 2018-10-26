# Change Log
All notable changes to the core module will be documented in this file.

#### 2.x Releases
- `2.2.x` Releases - [2.2.0](#220)
- `2.1.x` Releases - [2.1.0](#210)
- `2.0.x` Releases - [2.0.0](#200)
---

#### 1.x Releases
- `1.1.x` Releases - [1.1.0](#110)
---

## [2.2.0](https://maven.blockv.io/artifactory/webapp/#/artifacts/browse/tree/General/BLOCKv/io/blockv/sdk/core/2.2.0)
Released on 2018-10-26

#### Updated
- `Blockv` to use version 1.2.0 of [Face](/face) module.
  - Pull Request [#90](https://github.com/BLOCKvIO/android-sdk/pull/90)

## [2.1.0](https://maven.blockv.io/artifactory/webapp/#/artifacts/browse/tree/General/BLOCKv/io/blockv/sdk/core/2.1.0)
Released on 2018-10-23

#### Added

- Method to update a vAtom model using a StateUpdateEvent. 
  - Pull Request [#83](https://github.com/BLOCKvIO/android-sdk/pull/83)
- Image Progress Face when using the Face module. 
 - Pull Request [#85](https://github.com/BLOCKvIO/android-sdk/pull/85)
  
## [2.0.0](https://maven.blockv.io/artifactory/webapp/#/artifacts/browse/tree/General/BLOCKv/io/blockv/sdk/core/2.0.0)
Released on 2018-09-28

#### Updated

- Replaced Pack model with Vatom model.
  - Pull Request [#53](https://github.com/BLOCKvIO/android-sdk/pull/53)
- Replaced DiscoverPack model with Vatom model.
  - Pull Request [#56](https://github.com/BLOCKvIO/android-sdk/pull/56) 
- Moved common code into separate module.
  - Pull Request [#58](https://github.com/BLOCKvIO/android-sdk/pull/58) 
  
#### Upgrade Notes
This release has breaking changes.

- Models have been moved, `import io.blockv.common.model.*`.
- Builders have been moved, `import io.blockv.common.builder.*`.
- Callable has been moved, `import io.blockv.common.util.*`.
- The discover query builder no longer lets you set the return type. To get the count value use the `discoverCount` method.

## [1.1.0](https://maven.blockv.io/artifactory/webapp/#/artifacts/browse/tree/General/BLOCKv/io/blockv/sdk/core/1.1.0)
Released on 2018-07-10

#### Added

- Allow a vAtom to be sent to the trash.
  - Pull Request [#27](https://github.com/BLOCKvIO/android-sdk/pull/27) , [#32](https://github.com/BLOCKvIO/android-sdk/pull/32)
  
#### Updated
- Update code style
  - Pull Request [#26](https://github.com/BLOCKvIO/android-sdk/pull/26) , [#31](https://github.com/BLOCKvIO/android-sdk/pull/31) 
- Rename Group model to Pack
  - Pull Request [#28](https://github.com/BLOCKvIO/android-sdk/pull/28)
- Add paging to inventory
  - Pull Request [#30](https://github.com/BLOCKvIO/android-sdk/pull/30)
- Readme to reflect beta status.
  - Pull Request [#24](https://github.com/BLOCKvIO/android-sdk/pull/24) , [#29](https://github.com/BLOCKvIO/android-sdk/pull/24)

#### Upgrade Notes
This release has breaking changes.

- The `getInventory` method now uses paging, you need to specify a `page` and a `count` in addition to the `id`.
- The `Group` model is now named `Pack`, `DiscoverGroup` is now named `DiscoverPack`.