# Change Log
All notable changes to the rxcore module will be documented in this file.

#### 2.x Releases
- `2.0.x` Releases - [2.0.0](#110)
---

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
