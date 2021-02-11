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

#### 1. How to use the PMS Service:
The `start ()` method starts the PMS service.
```
//Required imports 
import br.ufc.mdcc.cmu.pmslib.PMS;
import br.ufc.mdcc.cmu.pmslib.PMSInterface;
import br.ufc.mdcc.cmu.pmslib.exception.PMSException;

PMSInterface pms = PMS.getInstance(this);
try {
    pms.start();
} catch (PMSException e) {
    e.printStackTrace();
}
```

#### 2.1. Implementing a CEP event and rule
The PMS service receives several events, so your client app needs to specify which events interesting it. To create a CEP event and rule, create a class that extends the ```CEPResource``` class. See the [example](https://github.com/makleyston-ufc/PMS/blob/main/PMSlib/app/src/main/java/br/ufc/mdcc/cmu/pmslib/cep/resources/GPSCEPResource.java).
```
public class GPSCEPResource extends CEPResource {
    public String lat;
    public String log;
    
    //Getters and setters

    public GPSCEPResource(){
        super();
        addDomain("/anemia");
        addDomain("/allergy");
        setType("LocalizationSensor");
    }

    public GPSCEPResource(OntModel ontModel){
        super(ontModel);
        GenericAnnotation genericAnnotation = new GenericAnnotation();
        genericAnnotation.setOntModel(ontModel);

        lat = genericAnnotation.returnValuePropertyString("http://www.w3.org/2003/01/geo/wgs84_pos/","lat");
        log = genericAnnotation.returnValuePropertyString("http://www.w3.org/2003/01/geo/wgs84_pos/","long");
    }

    @Override
    public String getStatement() {
        return "select o from GPSCEPResource as o where o.lat = 'xxxxx'";
    }
}
```
Note 1: CEP engine requires the setters and getters of attributes that were specified.

Note 2: Create two constructors: 
* A constructor without parameters and with specified topics (method ```addDomain()```) which the PMS service must publish, and the specified sensor types this CEPResource work.
* A constructor with a parameter of type OntModel. This parameter contains the sensor semantic annotation that generated the event.

Note 3: Implement the method ```getStatement()``` to return a CEP rule. 

#### 2.2. How to use my CEPResource implementations in PMS service?
```
PMSInterface pms = PMS.getInstance(this);

/*It's possible to insert several CEPResource*/
/*Adding a CEPResource*/
pms.addCEPResourceClass(GPSCEPResource.class);

try {
    pms.start();
} catch (PMSException e) {
    e.printStackTrace();
}
```

#### 3.1 How to implement a IoT Middleware and set it in the PMS Service?
PMS Service has two IoT middleware already implemented, Google Awareness and Loccam. However, it's possible to use several other IoT middleware just implementing a class of type `IoTMiddlewareAdapter`. See the [example](https://github.com/makleyston-ufc/PMS/blob/main/PMSlib/app/src/main/java/br/ufc/mdcc/cmu/pmslib/iotmiddleware/googleawareness/IoTMiddlewareAdapterImpl.java). 

Implement the listener to receive the IoT middleware data. See the [example](https://github.com/makleyston-ufc/PMS/blob/main/PMSlib/app/src/main/java/br/ufc/mdcc/cmu/pmslib/iotmiddleware/googleawareness/IoTMiddlewareListenerImpl.java).  

If you implemented a new IoT middleware, then you must to set the IoT middleware adapter implementation in the PMS Service. See the example:
```
PMSInterface pms = PMS.getInstance(this);

/*Create an instance of type IoTMiddlewareAdapter*/
IoTMiddlewareAdapterImpl ioTMiddlewareAdapter = new IoTMiddlewareAdapterImpl(this);
/*Create an instance of type IoTMiddlewareListener*/
IoTMiddlewareListenerImpl ioTMiddlewareListener = new IoTMiddlewareListenerImpl(this);
/*Setting the listener in the IoT middleware adapter*/
ioTMiddlewareAdapter.setIoTMiddlewareListener(ioTMiddlewareListener);
/*Setting the IoT middleware adapter in the PMS Service*/
pms.setIoTMiddlewareAdapter(ioTMiddlewareAdapter);

try {
    pms.start();
} catch (PMSException e) {
    e.printStackTrace();
}
```


#### 4. Implement a MQTT subscriber your preference to comsume the data published by PMS Service in the MQTT Broker. We suggest the MQTT Client [Paho](https://www.eclipse.org/paho/). 
