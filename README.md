# Kindle to Notion Syncer

*Kindle to Notion Syncer* synchronizes highlights from an Amazon Kindle device
to Notion, organizing these into a database.

##### Table of contents

* [About](#About)
    * [Features](#Features)
    * [Limitations](#Limitations)
* [Usage](#Usage)
    * [Prerequisites](#Prerequisites)
    * [Configuration](#Configuration)
    * [Execution](#Execution)
* [Support](#Support)
* [Disclaimer](#Disclaimer)

## About

### Features
* **Allows syncing of highlights not present in Kindle Cloud Reader**: this means
highlights of books or documents that have not been purchased from Amazon will 
be synced to Notion.
* **Works on every Amazon Kindle models**.

### Limitations
* **Requires the Amazon Kindle device connected via USB**.
* **Requires a manual creation of the Notion database**.

## Usage

### Prerequisites
* A Kindle device.
* A Notion account.
* [Docker](https://docs.docker.com/get-docker/).
### Configuration
  1. **Create a new Notion database**
    
      i. Create a new page by clicking *Add a page*. 
  
      ii. Create a new *Table* database and select *New database* as data source.
      
      iii. Create a new *Table* database and select *New database* as data source.
      
      iv. Create a property of type *Title* with name *Title*.
      
      v. Create a property of type *Text* with name *Author*.  


2. **Enable Notion integration and get API key**
   
     i. Open *Settings & Members* and select *Integrations*.
  
     ii. Select *Develop your own integrations* to redirect to the integration
        page in Notion web.
  
     iii. Select the New integration option, enter 
     **kindle-to-notion-syncer** as name of the integration,select the 
     workspace you want to use it with and select *Submit*.
    
    iv. Copy the secrets under *Internal Integration Token*.
    
    v. Go back to the database page in Notion and select the *Share* button; 
    select the created integration by its name and then select *Invite*.

### Execution
*Kindle to Notion Syncer* runs as Docker container; it can be executed running 
the following command:
```
docker run -e JAVA_OPTIONS="-Dnotion.book.database-id=<notion-database-id> -Dnotion.api-key=<notion-api-key>" -p 8080:8080 -v <kindle-documents-base-path>:/documents lucamarchi/notion-to-kindle-syncer:0.0.1
```
where:
* `<notion-database-id>` is the ID of the database where the highlights are 
synced; this can be easily retrieved by the URL of Notion Web.
* `<notion-api-key>` is the Notion API key.
* `<kindle-documents-base-path>` is the path of the _documents_ directory of 
the Kindle device (for MacOS, `/Volumes/Kindle/documents`).

#### Additional configuration
Some additional can be provided in the `JAVA_OPTIONS` options of the command 
above:
* `-Dnotion.book.title=<title>` where `<title>` is the name of 
  the _Title_ property of the Notion database; by default this is set to _Title_. 
* `-Dnotion.book.author=<author>` where `<author>` is the name of
  the _Author_ property of the Notion database; by default this is set to _Title_.

## Support
[!["Buy Me A Coffee"](https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png)](https://www.buymeacoffee.com/lucamarchi)

## Disclaimer
The author is not associated with Amazon.com, Inc. or with Notion Labs Inc.
