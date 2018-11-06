# Students API Integration Tests

This directory contains files that run integration tests against the Students API.

First, create a configuration.json file from configuration\_example.json.

Use this command to run the tests:
```shell
python integration_tests.py \
    -i /path/to/configuration.json \
    -s /path/to/openapi.yaml
```

### OpenAPI

These integration tests compare the API responses with the expected response
structures documented in the [OpenAPI specification](../../../swagger.yaml).

### Docker

Use these commands to build and run the tests in a container. All you need installed is Docker.
Make sure you are in the root directory of the repository.

```shell
$ docker build -t students-api-integration-tests -f src/test/integration/Dockerfile .
# Run the integration tests in Unix
$ docker run -v "$PWD"/src/test/integration/configuration.json:/usr/src/app/configuration.json:ro students-api-integration-tests
# Run the integration tests in Windows
$ docker run -v c:\path\to\configuration.json:/c:\usr\src\app\configuration.json:ro students-api-integration-tests
```

Python Version: 3.6.2
