# Introduction

Quarterdeck bundles several databased services for FredBoat.
At this point, this is not a public API of any sorts.
Any documentation found here is aimed at FreBoat developers.
Future development of the API might include additional, semi-public services.
This documentation is covering v1+ resources and conventions.

* * *

**CAUTION**: The Quarterdeck v1 API's status is **work in progress**. Things are open to be changed,
so do not build anything production grade relying on it yet,
but also please do not hesitate to provide feedback.

* * *

# Overview

## HTTP verbs
| Verb | Usage |
| ---- | ----- |
| `GET` | Used to retrieve a resource |
| `POST`| Used to create a new resource |
| `PATCH` | Used to update an existing resource, including partial updates |
| `DELETE` | Used to delete or reset an existing resource |

## HTTP status codes
Quarterdeck does not use any special http status codes and does not stray far away from the one true
path of RFCs detailing the usage and implementation of http status codes. In case of erroneous requests,
Quarterdeck tries to provide comprehensive error messages in its responses,
so you should keep an eye out for those. A full list of http status codes with
short explanations can be found [here](http://www.restapitutorial.com/httpstatuscodes.html),
however Quarterdeck will likely never use more than a small subset of those.

The most important ones are listed here:

| Status code | Usage |
| ----------- | ----- |
| `2xx` | Standard response for successful HTTP requests. The actual response will depend on the request method used. In a GET request, the response will contain an entity corresponding to the requested resource. In a POST or PATCH request, the response will contain an entity describing or containing the result of the action. A DELETE request will probably just be empty. |
| `4xx` | Something is wrong with your request. Pay attention to the concrete status code as well as the attached message, those will usually contain more information to help fix your request. |
| `5xx` | Something unexpected blew up on Quarterdecks side. Try again later, and if the 5xx persists, please [file an issue](https://github.com/FredBoat/Backend/issues). |



## Authentication
Quarterdeck uses [basic access authentication](https://tools.ietf.org/html/rfc7617).

## Authorization
Quarterdeck is currently targeted at FredBoat internal apps only. Each app gets a user and
pass assigned, giving it Admin access.

## Conventions
Besides doing a best effort to comply with the various existing RFCs out there,
here are a few conventions we adhere to:

- **Longs and any other number values taking up more than 32 bits are sent as a string.**
While JSON specs don't seem to set an explicit limit to what a number can be,
in practice there are clients that will parse a JSON number as a double,
leading to precision loss for higher long values.
- **camelCase for attributes.** camelCase is a Java and JavaScript convention.
It does clash with postgres' case insensitive and therefore snake_case tables and columns,
so if we are going to use changefeeds a function will have to be added to convert those.
- **Do not send null values. In some cases you might send null values to delete/reset an attribute/entity**
Avoid sending null values, unless explicitly allowed for an attribute / entity (see concrete docs below).
The API allows null values only in a few cases, and only to restore default values of an attribute/entity
(similar behaviour to firing a DELETE request for that entity).

## Limitations of these docs and gotchas

This documentation is based on springfox, which only supports swagger 2 for now. OpenAPI 3 support has been announced
for soon™. OpenAPI 3 allows for a bit more detailed definitions of models and attributes. Besides that, there are a few
other issues:
- When providing a position, for example to have the `guildId` attribute be shown at the top of a model for better
readability, the documentation will automatically mark it as `allowEmptyValue: false`. This is not the case. You are not
required to pass the `guildId` or similar ids in your RequestBody, if they can be determined from the path and query.
- When giving example values for snowflakes, which are handled as a string, but the current Discord implementation are
actually longs, the docs will render the value as a number, even though the type is string. To avoid confusion and
possibly wrong client implementations, no example values for snowflakes are provided. If you really have never seen
a Discord snowflake, here is an example guild id: `"214539058028740609"`. You can learn more about Discord snowflakes
[here](https://i.imgur.com/UxWvdYD.png)
