# Patient Monitoring Service - PMS Service
PMS Service offers an API to monitor sensors in the environment, gather data, annotate the data semantically (ontology-based), find patterns on the data stream, and publish the events founded (in format RDF) in the MQTT Broker to any/several client apps can consume it.

## How to import the PMS Service?
#### 1. Include in the `gradle.project` in `repositories`:

```maven { url "https://jitpack.io" }```

Example:
```
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

#### 2. Include in the `gradle.app` in `dependencies`:

```implementation 'com.github.makleyston-ufc.PMS:app:main-SNAPSHOT'```

Example:
```
dependencies {
    ...
    implementation 'com.github.makleyston-ufc.PMS:app:main-SNAPSHOT'
}

```

## How to use the PMS Service?
#### 1. Import the class in the Java code:
```
import br.ufc.mdcc.cmu.pmslib.PMS;
import br.ufc.mdcc.cmu.pmslib.PMSInterface;
import br.ufc.mdcc.cmu.pmslib.exception.PMSException;
```

#### 2. Initialize the PMS Service:
```
PMSInterface pms = PMS.getInstance(this);
try {
    pms.start();
} catch (PMSException e) {
    e.printStackTrace();
}
```

#### 3. Implement a MQTT subscriber your preference to comsume the data published by PMS Service in the MQTT Broker. We suggest the MQTT Client [Paho](https://www.eclipse.org/paho/). 
