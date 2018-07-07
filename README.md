![FredBoat](https://fred.moe/YY1.png)

# Quarterdeck [![TeamCity (full build status)](https://img.shields.io/teamcity/https/ci.fredboat.com/e/Quarterdeck_Build.svg?style=flat-square)](https://ci.fredboat.com/viewType.html?buildTypeId=Quarterdeck_Build&guest=1) [![Docker Pulls](https://img.shields.io/docker/pulls/fredboat/quarterdeck.svg)](https://fredboat.com/docs/selfhosting) [![Docker Layers](https://images.microbadger.com/badges/image/fredboat/quarterdeck:dev-v1.svg)](https://microbadger.com/images/fredboat/quarterdeck:dev-v1 "Get your own image badge on microbadger.com") [![Docker Version](https://images.microbadger.com/badges/version/fredboat/quarterdeck:dev-v1.svg)](https://microbadger.com/images/fredboat/quarterdeck:dev-v1 "Get your own version badge on microbadger.com") [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.fredboat.backend%3ABackend%3AQuarterdeck&metric=coverage)](https://sonarcloud.io/dashboard?id=com.fredboat.backend%3ABackend%3AQuarterdeck)

Please see the main repo for more information on the FredBoat project: [https://github.com/Frederikam/FredBoat](https://github.com/Frederikam/FredBoat)

This repository is home to FredBoat's internal database backend. At this point, this is not a public API of any sorts.
Any documentation found here is aimed at FreBoat developers. Future development of the API might include additional, semi-public services.


## Versions

### v1

##### Click [here](https://koakuma.fredboat.com/qd/swagger-ui.html) to find detailed online docs.  
The online documentation includes running test queries in the browser against a live staging deployment of Quarterdeck. 
If you are a developer who wants to build apps based on FredBoat's backend, please read the document 
[linked below](#contributing) to learn how to join our community, where you can request credentials to access
our testing environment.

### v0 [Deprecated]

v0 has been fully deprecated and removed. The v1 endpoints should fully support any existing use cases. 
If any routes are discovered to be missing please open an issue or PR. 

## Building And Testing Quarterdeck

To make a build, including running tests, do:
```
./gradlew build
```

To run tests against our postgres database, Quarterdeck requires docker and docker-compose installed, as well an 
unoccupied port `5433`.

When running tests repeatedly on a development machine you can shave off some time by passing a property 
to keep the postgres container alive between tests:

```
./gradlew test -DkeepPostgresContainer=true
```

or setting it via IntelliJ IDEA's run/debug config as a VM option:
![Setting the keep postgres container property with IntelliJ IDEA's run/debug config](https://fred.moe/rBL.png)

Keep in mind that in that case you will have to manually shut down the container to get rid of it:

```
docker stop quarterdecktest_db_1
docker rm quarterdecktest_db_1
```


## Contributing
The constribution guideline for this project can be found [here](CONTRIBUTING.md).


## Code of Conduct
The code of conduct for this project can be found [here](https://github.com/Frederikam/FredBoat/blob/dev/CODE_OF_CONDUCT.md).
