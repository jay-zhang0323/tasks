# Procedure
## Download and compile war
```
OhMyTiUP$ git clone https://github.com/springhow/spring-boot-war-example.git
ohMyTiUP$ cd sprint-boot-war-example.git
OhMyTiUP$ mvn package
OhMyTiUP$ sudo cp target/hello-world-0.0.1-SNAPSHOT.war /opt/www/
```

## Nginx unit install
```
OhMyTiUP$ sudo apt-get install unit unit-dev unit-jsc11
```

### Start the config
```
OhMyTiUP$ config.json
{
    "listeners": {
        "*:8080": {
            "pass": "applications/java"
        }
    },
    "applications": {
        "java": {
            "user": "unit",
            "group": "unit",
            "type": "java",
            "environment": {
                "Deployment": "0.0.1"
            },
            "classpath": [],
            "webapp": "/opt/www/hello-world-0.0.1-SNAPSHOT.war"
        }
    }
}
OhMyTiUP$ sudo curl -X PUT --data-binary "@config.json" --unix-socket /var/run/control.unit.sock http://localhost/config/
OhMyTiUP$ sudo curl --unix-socket /var/run/control.unit.sock http://localhost
{
        "certificates": {},
        "config": {
                "listeners": {
                        "*:8080": {
                                "pass": "applications/java"
                        }
                },

                "applications": {
                        "java": {
                                "user": "unit",
                                "group": "unit",
                                "type": "java",
                                "environment": {
                                        "Deployment": "0.0.1"
                                },

                                "classpath": [],
                                "webapp": "/opt/www/hello-world-0.0.1-SNAPSHOT.war"
                        }
                }
        },

        "status": {
                "connections": {
                        "accepted": 2,
                        "active": 0,
                        "idle": 0,
                        "closed": 2
                },

                "requests": {
                        "total": 2
                },

                "applications": {
                        "java": {
                                "processes": {
                                        "running": 1,
                                        "starting": 0,
                                        "idle": 1
                                },

                                "requests": {
                                        "active": 0
                                }
                        }
                }
        }
}
```
