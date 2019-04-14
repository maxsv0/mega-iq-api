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

Production proxy: https://api.mega-iq.com

Localized end-point URL: 
* http://en2.mega-iq.com/api/v1 (EN)
* http://de.mega-iq.com/api/v1 (DE)
* http://ru.mega-iq.com/api/v1 (RU)

## Testing

`mvn verify`

## API Documentation

### Status message
URL: https://api.mega-iq.com/

Type: GET

Result:
```JSON
{
  "ok": true,
  "msg": "Mega-IQ API v.1 Build 20190331-1138.417155479376758790 Google App Engine/1.9.71",
  "date": "2019-04-14T11:16:43.350+0000",
  "locale": "EN"
}
```

### Start the test
URL: https://api.mega-iq.com/test/start?type=[type]

Type: GET, Auth token required

Param: type, possible values: PRACTICE_IQ, STANDARD_IQ, MEGA_IQ, MATH, GRAMMAR

Result:
```JSON
{
  "ok": true,
  "msg": null,
  "date": "2019-04-14T11:23:40.338+0000",
  "locale": "DE",
  "test": {
    "code": "6f148d8a-6119-49ed-8b9d-b869422d53ba",
    "url": "/iqtest/result/6f148d8a-6119-49ed-8b9d-b869422d53ba",
    "type": "PRACTICE_IQ",
    "locale": "DE",
    "status": "ACTIVE",
    "createDate": "2019-04-14T11:23:38.872+0000",
    "questionSet": [
      {
        "pic": "https://storage.googleapis.com/mega-iq/q/q3066.png",
        "pic2x": "https://storage.googleapis.com/mega-iq/q/q3066@2x.png",
        "answerCorrect": null,
        "answerUser": null,
        "title": "Finden Sie das redundante Stück Bild",
        "description": null,
        "updateDate": null,
        "answers": [
          {
            "id": 3393,
            "pic": "https://storage.googleapis.com/mega-iq/a/q3066img3.png",
            "pic2x": "https://storage.googleapis.com/mega-iq/a/q3066img3@2x.png"
          },
          {
            "id": 3394,
            "pic": "https://storage.googleapis.com/mega-iq/a/q3066img4.png",
            "pic2x": "https://storage.googleapis.com/mega-iq/a/q3066img4@2x.png"
          },
          .....
        ]
      },
      .....
    ]
  }
}
```

### Submit the answer

URL: https://api.mega-iq.com/test/[uuid]

Type: POST, Auth token required

Body: `{"question":1,"answer":3394}`

Result:
```JSON
.....
"questionSet": [
      {
        "pic": "https://storage.googleapis.com/mega-iq/q/q3066.png",
        "pic2x": "https://storage.googleapis.com/mega-iq/q/q3066@2x.png",
        "answerCorrect": null,
        "answerUser": 3393,
        "title": "Finden Sie das redundante Stück Bild",
        "description": null,
        "updateDate": null,
        "answers": [
        .....
        ]
      },
.....
```


### Submit finish test

URL: https://api.mega-iq.com/test/finish?testCode=[uuid]

Type: GET, Auth token required

Result:
```JSON
{
  "ok": true,
  "msg": null,
  "date": "2019-04-14T11:32:37.209+0000",
  "locale": "DE",
  "test": {
    "code": "6f148d8a-6119-49ed-8b9d-b869422d53ba",
    "url": "/iqtest/result/6f148d8a-6119-49ed-8b9d-b869422d53ba",
    "type": "PRACTICE_IQ",
    "locale": "DE",
    "status": "FINISHED",
    "createDate": "2019-04-14T11:23:38.872+0000",
    "updateDate": "2019-04-14T11:32:36.638+0000",
    "finishDate": "2019-04-14T11:32:36.638+0000",
    "points": 1,
    "questionSet": [
      {
        "pic": "https://storage.googleapis.com/mega-iq/q/q3066.png",
        "pic2x": "https://storage.googleapis.com/mega-iq/q/q3066@2x.png",
        "answerCorrect": 3396,
        "answerUser": 3394,
        "title": "Finden Sie das redundante Stück Bild",
        "description": "NULL",
        "updateDate": null,
        "answers": [
          {
            "id": 3393,
            "pic": "https://storage.googleapis.com/mega-iq/a/q3066img3.png",
            "pic2x": "https://storage.googleapis.com/mega-iq/a/q3066img3@2x.png"
          },
          {
            "id": 3394,
            "pic": "https://storage.googleapis.com/mega-iq/a/q3066img4.png",
            "pic2x": "https://storage.googleapis.com/mega-iq/a/q3066img4@2x.png"
          },
          .....
        ]
      },
      .....
    ]
  }
}
```

### Get public test info

URL: https://api.mega-iq.com/test/[uuid]

Type: GET

Result:
```JSON
{
  "ok": true,
  "msg": null,
  "date": "2019-04-14T11:36:28.274+0000",
  "locale": "DE",
  "test": {
    "code": "6f148d8a-6119-49ed-8b9d-b869422d53ba",
    "url": "/iqtest/result/6f148d8a-6119-49ed-8b9d-b869422d53ba",
    "type": "PRACTICE_IQ",
    "locale": "DE",
    "status": "FINISHED",
    "finishDate": "2019-04-14T11:32:36.638+0000",
    "points": 1
  }
}
```

### Get my test results

URL: https://api.mega-iq.com/list-my

Type: GET, Auth token required

Result:
```JSON
{
  "ok": true,
  "msg": null,
  "date": "2019-04-14T11:39:22.737+0000",
  "locale": null,
  "tests": [
    {
      "code": "6f148d8a-6119-49ed-8b9d-b869422d53ba",
      "url": "/iqtest/result/6f148d8a-6119-49ed-8b9d-b869422d53ba",
      "type": "PRACTICE_IQ",
      "locale": "DE",
      "status": "FINISHED",
      "createDate": "2019-04-14T11:23:38.872+0000",
      "updateDate": "2019-04-14T11:32:36.638+0000",
      "finishDate": "2019-04-14T11:32:36.638+0000",
      "points": 1,
      "questionSet": []
    },
    .....
  ],
  "user": {
    "id": 66,
    "name": "Test 3",
    "pic": "https://lh3.googleusercontent.com/tuw6slWlwIeL3PewrRnDPVTfpuR5OPrDsMTNmDQnb3KQDBFqsuJl8MFfNAkCVXkPcmz0BoM6rvw2XxE10eGX",
    "url": "/user/66",
    "age": 22,
    "iq": null,
    "location": "Germany",
    "email": "max.svistunov+test003@gmail.com",
    "password": null,
    "token": "iJKV1QilY3VyZXRva2VuQiOjE1NTUyNDE5NjEsImV4cCI6MTU1NTt7L9Tg_-BO5Ni1vFK-a",
    "isPublic": true,
    "isEmailVerified": false
  }
}
```


### Delete test result

URL: https://api.mega-iq.com/test/[uuid]

Type: DELETE, Auth token required

Result:
```JSON
{
  "ok": true,
  "msg": "Test result successfully deleted",
  "date": "2019-04-14T11:39:27.065+0000",
  "locale": "DE"
}
```


### Create file upload URL
URL: https://api.mega-iq.com/storage/create

Type: GET

Result:
```JSON
{
  "ok": true,
  "msg": "/_ah/upload/AMu6YHXIvV8vI1MDp7GDztHSFut9O6rEa-qVKF_Db4EJF0SXoQcZYAAAwdhs8GNS6q/",
  "date": "2019-04-14T11:19:08.020+0000",
  "locale": "EN"
}
```

### Serve storage file
URL: https://api.mega-iq.com/storage/serve?key=[key]

Type: GET

Result:
```JSON
{
  "ok": true,
  "msg": "https://lh3.googleusercontent.com/S0bMA7OfXg5StTV4yJiwM9DMQg-9L6LPaKjypxfdLMFTKpTJtnhnI8Kv3Np3OEz1MZ5n8trp9NTZQvJq0MyT",
  "date": "2019-04-14T11:21:19.550+0000",
  "locale": "DE"
}
```
