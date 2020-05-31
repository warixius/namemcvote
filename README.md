# Example Plugin - NameMC Vote Management

## The Aim of this Plugin

This project should demonstrate the programming skills of the developer. It is not to be used in a real situation as there is no real use case for creating such a new plugin.

## System Architecture

The plugin establishes two connections:

- A MySQL database connection for persistence
- HTTP REST Calls to https://api.namemc.com to gather data for verifying likes

Any settings to the plugin can be done in the corresponding directory.

## How it works

You can use the following commands within this plugin

- ``/namemcvote help`` to get a help page for this plugin
- ``/namemcvote vote`` If the user has not voted for the server, it will send a link, else the user is given a monetary reward, if it hasn't been given (10.000 Bucks). 
- ``/namemcvote check <Name>`` To verify whether someone voted for a server.
- ``/namemcvote list`` to get a list of all users who voted for a server on NameMC [Admin]
- ``/namemcvote reward set <Amount>`` to set the amount to be received. [Admin]

It uses a database (MySQL 5.7) to save the people who have voted for the server and received a reward. 
The config file is saved on the client, for demonstration purposes.