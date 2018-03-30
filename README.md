![FredBoat](https://fred.moe/YY1.png)

# Quarterdeck [![TeamCity (full build status)](https://img.shields.io/teamcity/https/ci.fredboat.com/e/Quarterdeck_Build.svg?style=flat-square)](https://ci.fredboat.com/viewType.html?buildTypeId=Quarterdeck_Build&guest=1) [![Docker Pulls](https://img.shields.io/docker/pulls/fredboat/quarterdeck.svg)](https://fredboat.com/docs/selfhosting) [![Docker Layers](https://images.microbadger.com/badges/image/fredboat/quarterdeck:dev-v1.svg)](https://microbadger.com/images/fredboat/quarterdeck:dev-v1 "Get your own image badge on microbadger.com") [![Docker Version](https://images.microbadger.com/badges/version/fredboat/quarterdeck:dev-v1.svg)](https://microbadger.com/images/fredboat/quarterdeck:dev-v1 "Get your own version badge on microbadger.com")

Please see the main repo for more information on the FredBoat project: [https://github.com/Frederikam/FredBoat](https://github.com/Frederikam/FredBoat)

This repository is home to FredBoat's internal database backend. At this point, this is not a public API of any sorts.
Any documentation found here is aimed at FreBoat developers. Future development of the API might include additional, semi-public services.


## Versions

The API is currently versioned as v0, which is a straight port of the existing database operations.  
The endpoints are split up by [existing entities](https://github.com/Frederikam/FredBoat/tree/dev/Database/src/main/java/fredboat/db/entity).

The plans for v1 and future versions include:
- proper usage of http verbs (GET, DELETE, PATCH, etc)
- decoupling from the internal entity model, instead granular calls
- a success/error layer
- end-user access and authorization

### v0

The existing entity endpoints are:
- /blacklist
- /guildconfig
- /guilddata
- /guildmodules
- /guildperms
- /prefix
- /searchresult

Each entity endpoint supports the following operations:
- POST /fetch
- POST /merge
- POST /delete

`fetch` and `delete` require the id to be sent, `merge` the entity itself.  

Some entity endpoints have additional calls:
- Blacklist:
  - GET /loadall

- Prefix:
  - POST /getraw

- Search result:
  - POST /getmaxaged

The entity endpoints are reachable behind the version path, for example:
```
GET http[s]://backend.url[:port]/v0/blacklist/loadall
```


There is an additional endpoint, that will return the supported versions of the API:
- GET /info/api/versions

Entities and ids are marshalled via [Gson](https://github.com/google/gson) on both sides.  
Authentication happens via [Basic access authentication](https://en.wikipedia.org/wiki/Basic_access_authentication).  
No kind of authorization is existent, you either have access, or you don't. In terms of security, treat this the same way you would treat a redis instance.  


### v1 [WIP]

Proposed path pattern for guild and user bound entities, which is similar to how the Discord API looks:
```
/v1/guilds/{id}/{entity}
/v1/users/{id}/{entity}
etc
```

Concrete examples:
```
/v1/guilds/174820236481134592/config
/v1/users/166604053629894657/aliases
```

Supporting the following http requests:
GET: return the existing or default entity
DELETE: reset the entity
PATCH: update one, several, or all attributes of an entity




## Conventions

Besides doing a best effort to comply with the various existing RFCs out there, here are a few conventions we adhere to
from v1 ongoing:
- Java longs and any other number values taking up more than 32 bits are sent as a String.
While JSON specs don't seem to set an explicit limit to what a number can be, in practice 
there are clients that will parse a JSON number as a double, leading to precision loss for
higher long values.
- camelCase for attributes. camelCase is a Java and Javascript convention. It does clash with postgres' case insensitive 
and therefore snake_case tables and columns, so if we using changefeeds a function will have to be added to convert those.


## Building Quarterdeck
To run tests against our postgres database, quarterdeck requires docker and docker-compose installed, as well an 
unoccupied port 5433.


## Contributing
If you are interested, you can read about contributing to this project [here](https://github.com/Frederikam/FredBoat/blob/master/CONTRIBUTING.md).


## Code of Conduct
The code of conduct for this project can be found [here](https://github.com/Frederikam/FredBoat/blob/master/CODE_OF_CONDUCT.md).
