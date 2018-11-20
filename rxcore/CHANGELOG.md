# Change Log
All notable changes to the rxcore module will be documented in this file.

#### 3.x Releases
- `3.0.x` Releases - [3.0.0](#300)
---

#### 2.x Releases
- `2.3.x` Releases - [2.3.0](#230)
- `2.2.x` Releases - [2.2.0](#220)
- `2.1.x` Releases - [2.1.0](#210)
- `2.0.x` Releases - [2.0.0](#200)
---

## [3.0.0](https://maven.blockv.io/artifactory/webapp/#/artifacts/browse/tree/General/BLOCKv/io/blockv/sdk/rxcore/3.0.0)
Released on 2018-xx-xx

#### Updated
- `Blockv` to use version 3.0.0 of [RxFace](/rxface) module and version 2.0.0 of [Common](/common) module.
  - Pull Request [#115](https://github.com/BLOCKvIO/android-sdk/pull/115)
- `Blockv`'s constructor to take extra param `Context` .
  - Pull Request [#115](https://github.com/BLOCKvIO/android-sdk/pull/115)

## [2.3.0](https://maven.blockv.io/artifactory/webapp/#/artifacts/browse/tree/General/BLOCKv/io/blockv/sdk/rxcore/2.3.0)
Released on 2018-11-06

#### Added
- Included Layered Image Face.
   - Pull Request [#109](https://github.com/BLOCKvIO/android-sdk/pull/109)

## [2.2.0](https://maven.blockv.io/artifactory/webapp/#/artifacts/browse/tree/General/BLOCKv/io/blockv/sdk/rxcore/2.2.0)
Released on 2018-10-26

#### Updated
- `Blockv` to use version 2.1.2 of [RxFace](/rxface) module.
  - Pull Request [#94](https://github.com/BLOCKvIO/android-sdk/pull/94)

## [2.1.0](https://maven.blockv.io/artifactory/webapp/#/artifacts/browse/tree/General/BLOCKv/io/blockv/sdk/rxcore/2.1.0)
Released on 2018-10-23

#### Added

- Face Module
  - Pull Request [#81](https://github.com/BLOCKvIO/android-sdk/pull/81)
- Method to update a vAtom model using a StateUpdateEvent. 
  - Pull Request [#86](https://github.com/BLOCKvIO/android-sdk/pull/86)
  

## [2.0.0](https://maven.blockv.io/artifactory/webapp/#/artifacts/browse/tree/General/BLOCKv/io/blockv/sdk/rxcore/2.0.0)
Released on 2018-09-28

#### Updated

- Replaced Pack model with Vatom model.
  - Pull Request [#53](https://github.com/BLOCKvIO/android-sdk/pull/53)
- Replaced DiscoverPack model with Vatom model.
  - Pull Request [#56](https://github.com/BLOCKvIO/android-sdk/pull/56) 
- Moved common code into separate module.
  - Pull Request [#58](https://github.com/BLOCKvIO/android-sdk/pull/58) 
- Rename module to rxcore.
  - Pull Request [#71](https://github.com/BLOCKvIO/android-sdk/pull/71)
  
#### Upgrade Notes
This release has breaking changes.

- The module was renamed from `rx` to `rxcore`, `import io.blockv.rxcore.*`.
- Models have been moved, `import io.blockv.common.model.*`.
- Builders have been moved, `import io.blockv.common.builder.*`.
- Callable has been moved, `import io.blockv.common.util.*`.
- The discover query builder no longer lets you set the return type. To get the count value use the `discoverCount` method.
