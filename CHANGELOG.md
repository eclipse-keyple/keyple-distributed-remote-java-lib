# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [2.5.1] - 2024-09-19
### Fixed
- Fixed the backward compatibility for clients that do not transmit the `apiLevel` field (issue [#15]).
### Upgraded
- Keyple Distributed Network Lib `2.5.0` -> `2.5.1`

## [2.5.0] - 2024-09-06
### Added
- Optimizes the "Reader Client Side" usage mode. 
  When a remote service is requested, 
  the client sends the server information on whether the local reader is in contact or contactless mode. 
  This reduces the number of network exchanges.
  This optimization will only be effective if the client is running version `2.5+` of the
  [Keyple Distributed Local Library](https://keyple.org/components-java/distributed/keyple-distributed-local-java-lib/).
### Upgraded
- Keyple Distributed Remote API `3.0.1` -> `3.1.0`
- Keyple Distributed Network Lib `2.4.0` -> `2.5.0`

## [2.4.0] - 2024-06-03
### Added
- Addition of two methods for configuring the timeout value used by server nodes in "Reader Client Side" usage mode
  (issue [eclipse-keyple/keyple-distributed-network-java-lib#13]).
  The timeout defines the maximum time the client can wait for the server's response, 
  as well as the maximum time the server can wait between two client calls.
  - `RemotePluginServerFactoryBuilder.NodeStep.withSyncNode(int timeoutSeconds)`
  - `RemotePluginServerFactoryBuilder.NodeStep.withAsyncNode(AsyncEndpointServerSpi endpoint, int timeoutSeconds)`
### Changed
- Logging improvement.
### Upgraded
- Keyple Distributed Network Lib `2.3.1` -> `2.4.0`

## [2.3.1] - 2024-04-12
### Changed
- Java source and target levels `1.6` -> `1.8`
### Upgraded
- Keyple Common API `2.0.0` -> `2.0.1`
- Keyple Distributed Remote API `3.0.0` -> `3.0.1`
- Keyple Distributed Network Lib `2.3.0` -> `2.3.1`
- Keyple Util Lib `2.3.1` -> `2.4.0`
- Gradle `6.8.3` -> `7.6.4`

## [2.3.0] - 2023-11-28
### Added
- Added a property indicating the Distributed JSON API level in exchanged JSON data (current value: `"apiLevel": 2`).
- Added project status badges on `README.md` file.
### Fixed
- CI: code coverage report when releasing.
### Upgraded
- Keyple Distributed Remote API `2.1.0` -> `3.0.0`
- Keyple Distributed Network Library `2.2.0` -> `2.3.0`
- Keyple Util Library `2.3.0` -> `2.3.1` (source code not impacted)

## [2.2.1] - 2023-05-05
### Fixed
- Fixes the communication issue between client and server components when using the "Reader Client Side" usage mode 
  that appeared with version `2.2.0`.

## [2.2.0] - 2023-04-04
:warning: **CAUTION**: When using the "Reader Server Side" usage mode, it is requires to use at least version 
`2.2.0` of the [Keyple Distributed Local Library](https://keyple.org/components-java/distributed/keyple-distributed-local-java-lib/)!
### Changed
- Initial card content and user input/output data used for "ReaderClientSide" mode are now serialized/de-serialized
  as JSON objects, and no more as strings containing JSON objects.
### Upgraded
- "Keyple Distributed Network Library" to version `2.2.0`.

## [2.1.0] - 2023-02-17
### Added
- The possibility to provide to the `RemotePluginServer` a custom executor service to be used to asynchronously notify 
  remote reader connection events (see new method 
  `RemotePluginServerFactoryBuilder.builder(String remotePluginName, ExecutorService executorService)`).
- `CHANGELOG.md` file (issue [eclipse-keyple/keyple#6]).
- CI: Forbid the publication of a version already released (issue [#3])
### Upgraded
- "Keyple Distributed Remote API" to version `2.1.0`
- "Keyple Util Library" to version `2.3.0`
- "Google Gson Library" (com.google.code.gson) to version `2.10.1`

## [2.0.0] - 2021-10-06
This is the initial release.
It follows the extraction of Keyple 1.0 components contained in the `eclipse-keyple/keyple-java` repository to dedicated 
repositories.
It also brings many major API changes.

[unreleased]: https://github.com/eclipse-keyple/keyple-distributed-remote-java-lib/compare/2.5.1...HEAD
[2.5.1]: https://github.com/eclipse-keyple/keyple-distributed-remote-java-lib/compare/2.5.0...2.5.1
[2.5.0]: https://github.com/eclipse-keyple/keyple-distributed-remote-java-lib/compare/2.4.0...2.5.0
[2.4.0]: https://github.com/eclipse-keyple/keyple-distributed-remote-java-lib/compare/2.3.1...2.4.0
[2.3.1]: https://github.com/eclipse-keyple/keyple-distributed-remote-java-lib/compare/2.3.0...2.3.1
[2.3.0]: https://github.com/eclipse-keyple/keyple-distributed-remote-java-lib/compare/2.2.1...2.3.0
[2.2.1]: https://github.com/eclipse-keyple/keyple-distributed-remote-java-lib/compare/2.2.0...2.2.1
[2.2.0]: https://github.com/eclipse-keyple/keyple-distributed-remote-java-lib/compare/2.1.0...2.2.0
[2.1.0]: https://github.com/eclipse-keyple/keyple-distributed-remote-java-lib/compare/2.0.0...2.1.0
[2.0.0]: https://github.com/eclipse-keyple/keyple-distributed-remote-java-lib/releases/tag/2.0.0

[#15]: https://github.com/eclipse-keyple/keyple-distributed-remote-java-lib/issues/15
[#3]: https://github.com/eclipse-keyple/keyple-distributed-remote-java-lib/issues/3

[eclipse-keyple/keyple-distributed-network-java-lib#13]: https://github.com/eclipse-keyple/keyple-distributed-network-java-lib/issues/13

[eclipse-keyple/keyple#6]: https://github.com/eclipse-keyple/keyple/issues/6