# JMS comsumer

#### Description
Micrsoservice that consumes Text JMS messages and prints them into console.

#### Build
Use SBT for building:
```sh
$ sbt reload clean compile test:compile jacoco:check scalastyle doc jacoco:report universal:packageBin
```

#### Install
Perform following steps:
- Extract _target/universal/jms-consumer-1.0.0.zip_.
- Goto _jms-consumer-1.0.0_ installation folder.
- In folder _bin/_ create _application.properties_ file with following content:
>spring.profiles.active=prod
> 
>jms.activemq.host=
>jms.activemq.topic=

#### Configure
Configuration consists of setting values for properties in _application.properties_ file. Table below shows properties that can be set, together with their default values.

| name | meaning | example | deafult value |
| ---- | ------- | ------- | ------------- |
| _spring.profiles.active_ | Spring active profile | should not be changed | _prod_ |
| _jms.activemq.host_ | Apache Active MQ broker URL | _tcp://localhost:61616/_ | _vm://localhost?broker.persistent=false&broker.useShutdownHook=false_ |
| _jms.activemq.topic_ | name of the topic to subscribe to | _some-topic_ | _text-topic_ |

#### Run
Got to _jms-consumer-1.0.0/bin_ folder and run:
```sh
$ ./jms-consumer
```
