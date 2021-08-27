# Keycloak Configurator
Keycloak Configurator is a tool written for java 11 which enabls you to adjust the `standalone.xml` configuration of keycloak to add reverse proxy and postgresql support.

This project is being used in the documentation: [docs.secshell.net/en/services/keycloak](https://docs.secshell.net/en/2._Services/2_keycloak/)

## Download
```sh
VERSION=1.0.3
apk add --update --no-cache alpine
wget https://github.com/secshellnet/keycloak-configurator/releases/download/v${VERSION}/keycloak-configurator-1.0-SNAPSHOT-all.jar -O /root/keycloak-configurator.jar
```


## Usage
Add reverse proxy support:
```sh
java -jar keycloak-configurator.jar /path/to/standalone.xml 
```

Add reverse proxy and postgresql support:
```sh
java -jar keycloak-configurator.jar /path/to/standalone.xml postgres.secshell.net keycloak_database_name keycloak_username keycloak_p4ssw0rd
```