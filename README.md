# LuxAdr Service
 LuxAdr provides a state of the art API to access data related to all official addresses of Luxembourg. 
 The data structure is closely related to the official data provided by [data.public.lu](https://data.public.lu) and can be kept up 
 to date with ease. Among others you will have access to all:

- Localities
- Postcodes
- Streets
- House numbers (Buildings)
- Location data of the buildings (latitude, longitude)

Several search features are included and designed to be used, among others, from web pages. 
You will also have the possibility to search via location data. Additionally once imported, data will never be deleted, 
only deactivated, so if you make a reference to an id it will be always be available for later use. 

![Admin setup screen](./Docs/AdminSetup-thumb.png?raw=true) ![Admin demo](./Docs/AdminDemo-thumb.png?raw=true) ![OpenAPI doc](./Docs/OpenAPIDoc-thumb.png?raw=true)

[Admin setup screenshot](./Docs/AdminSetup.png?raw=true) / [Admin demo screenshot](./Docs/AdminDemo.png?raw=true) / [OpenAPI doc screenshot](./Docs/OpenAPIDoc.png?raw=true)

## Details

The project consists of two separately deployable WAR. The main WAR **LuxAdrService** consits of the following funtionalities:
- Public REST API
- OpenAPI documentation of the public REST API using Swagger for displaying/testing
- Admin REST API to setup and configure the service (can be secured)

This service can be used by other web applications or web pages and is not meant to have a fancy user interface. 

The second WAR **LuxAdrAdmin** can be separately deployed (even on another server) and be used to do the first time setup and do regular data refreshes. In fact it is only a simple web page which dows not require deployment, but for simplicity sakes, it is.

The project itself here does not contain any address data but it downloads it from the newest available official datasets:
- [Registre national des localités et des rues](https://data.public.lu/fr/datasets/registre-national-des-localites-et-des-rues/)
- [Adresses georéférencées](https://data.public.lu/fr/datasets/adresses-georeferencees-bd-adresses/)

The project is kept database and application server agnostic and so does not make use of advanced features of current platforms (geo queries, ...). To compensate for these and to introduce a worthy search we make use of **Apache Lucene**. The use of Lucene is optional, but recommended for full functionality. 

## Requirements

- Java 8
- Java EE 7/8 application server
- Database

Tested on Payara 4 and 5 using Derby and Postgres. The admin interface requries a modern web browser (Firefox/Chrome).

## Setup

Build the project using your favorite IDE or directly using Maven. 

#### Docker compose

In the `Docker` folder you will find a docker compose file and a docker image file to easily setup the project:
1. Copy over the `Docker` folder contents to your server/machine
2. Copy the generated WAR files into the `deploy` folder
3. Optionally adjust the forwardes ports localted in `docker-compose.yml`
4. Build the containers using `docker-compose build`
5. Start the containers using `docker-compose up -d`

#### Manual

Setup your favorite application server and database. After that:
1. Create the jdbc resource `jdbc/sample` (some servers have it already linked with derby)
2. Make sure that you http connection timeout is high enough (see bellow)

#### After deployment

After the initial deployment time the projects are available under their respective URLs:
- http://HOSTNAME:PORT/LuxAdrAdmin
- http://HOSTNAME:PORT/LuxAdrService

Navigate to **LuxAdrAdmin** using a modern browser and follow the first time setup guide which will to an initial data import and secure the admin API. When the setup is done feel free to test out the API.

To get you started a [Postman](https://www.getpostman.com) collection is provided in the `Docs` folder.

## FAQ

> Importing aborts / takes too long

The data import can take a while depending on the available resources and the database you are using. It can take more then 10 minutes per step, but can also be as fast as 2 minutes. Hence the setting of the http timeout as the import data calls are currently blocking. If your http call timeouts, import will stop.

> Can everybody use this project?

Yes you can, refer to the Apache2 licence. I would be glad if you would credit me when using it.

