Mega-IQ 3.0 for Google App Engine (Java 8)
============================

Mega-IQ Spring Boot + Google App Engine application.

## MEGA-IQ 3.0. Requirements

## [Requirements Document](https://docs.google.com/document/d/1juGpnjcJOHJY45edddpCGH7KFqlJFZyafU-qRk0eF3s/edit?usp=sharing)

## Maven
### Running locally

`mvn appengine:run`

To use vist: http://localhost:8080/

### Deploying

`mvn -Dapp.deploy.projectId=megaiq637 -Dapp.deploy.version=$(date "+%Y%m%d-%H%M") clean package com.google.cloud.tools:appengine-maven-plugin:deploy`

To use visit:  https://megaiq637.appspot.com

Production proxy: http://api.mega-iq.com

Localized end-point URL: 
* http://en2.mega-iq.com/api/v1 (EN)
* http://de.mega-iq.com/api/v1 (DE)
* http://ru.mega-iq.com/api/v1 (RU)

## Testing

`mvn verify`
