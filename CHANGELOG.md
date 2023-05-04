# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [2.2.0] - 2023-04-04
:warning: **CAUTION**: this version requires to use at least version `2.2.0` of the
[Keyple Distributed Local Library](https://keyple.org/components-java/distributed/keyple-distributed-local-java-lib/)!
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
- `CHANGELOG.md` file (issue [eclipse/keyple#6]).
- CI: Forbid the publication of a version already released (issue [#3])
### Upgraded
- "Keyple Distributed Remote API" to version `2.1.0`
- "Keyple Util Library" to version `2.3.0`
- "Google Gson Library" (com.google.code.gson) to version `2.10.1`

## [2.0.0] - 2021-10-06
This is the initial release.
It follows the extraction of Keyple 1.0 components contained in the `eclipse/keyple-java` repository to dedicated 
repositories.
It also brings many major API changes.

[unreleased]: https://github.com/eclipse/keyple-distributed-remote-java-lib/compare/2.2.0...HEAD
[2.2.0]: https://github.com/eclipse/keyple-distributed-remote-java-lib/compare/2.1.0...2.2.0
[2.1.0]: https://github.com/eclipse/keyple-distributed-remote-java-lib/compare/2.0.0...2.1.0
[2.0.0]: https://github.com/eclipse/keyple-distributed-remote-java-lib/releases/tag/2.0.0

[#3]: https://github.com/eclipse/keyple-distributed-remote-java-lib/issues/3

[eclipse/keyple#6]: https://github.com/eclipse/keyple/issues/6