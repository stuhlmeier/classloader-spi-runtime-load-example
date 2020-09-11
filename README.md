# SPI runtime load/unload example

This is an example project intended to demonstrate loading, unloading and replacement of service provider implementations from JAR files at runtime.

This project contains 3 submodules:

- `example-service`
  - This module defines a `StringService` interface with a single method.
- `example-impl-1`
  - This module defines a service implementation that returns the device's hostname.
- `example-impl-2`
  - This module defines a service implementation that returns the current timestamp.
- `example-impl-3`
  - This module defines a service implementation intended to replace `example-impl-1` at runtime.
- `example-client`
  - This module loads and uses all service implementations.

You can run this project by executing `run_demo.sh`.
