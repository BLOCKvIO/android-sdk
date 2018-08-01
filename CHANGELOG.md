# Change Log
All notable changes to this project will be documented in this file.

#### 1.x Releases (API v1)
- `1.1.x` Releases - [1.1.0](#110)
---

## [1.1.0](https://github.com/BLOCKvIO/android-sdk/releases/tag/v1.1.0)
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