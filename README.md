# Patient Monitoring Service Library - PMS lib
PMS lib offers an API to monitor sensors in the environment, gather data, annotate the data semantically (ontology-based), find patterns on the data stream, and publish the events founded (in format RDF) in the MQTT Broker to any/several client apps can consume it.

## How to import the PMS lib?
#### 1. Include in the `gradle.project`'s `repositories` section:

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

#### 2. Include in the `gradle.app`'s `dependencies` section:

```implementation 'com.github.makleyston-ufc.PMS:app:v0.2'```

Example:
```
dependencies {
    ...
    implementation 'com.github.makleyston-ufc.PMS:app:v0.2'
}

```

## How to use the PMS lib?

#### 1. Importing and starting the PMS lib's services.
The `start()` method starts the PMS lib's services.
```
/*Required imports*/
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

#### 2.1. Implementing a CEP event and rule.
PMS lib receives several events, so your client app needs to specify which events interesting it. To create a CEP event and rule, create a class that extends the [`CEPResource`](https://github.com/makleyston-ufc/PMS/blob/main/PMSlib/app/src/main/java/br/ufc/mdcc/cmu/pmslib/cep/CEPResource.java) class. See the [example](https://github.com/makleyston-ufc/PMS/blob/main/PMSlib/app/src/main/java/br/ufc/mdcc/cmu/pmslib/cep/resources/GPSCEPResource.java).
```
public class GPSCEPResource extends CEPResource {
    public String lat;
    public String log;
    
    /*Getters and setters*/

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
* A constructor without parameters and with specified topics (use the `addDomain()` method) which the PMS lib must publish, and the specified sensor types this CEPResource work.
* A constructor with a parameter of type `OntModel`. This parameter contains the sensor semantic annotation that generated the event.

Note 3: Implement the method `getStatement()` to return a CEP rule in `String` format. 

#### 2.2. Using my CEPResource implementations in the PMS lib.
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

#### 3.1 How to implement a IoT Middleware adapter and set it in the PMS lib?
PMS lib has two IoT middleware already implemented, Google Awareness and Loccam. If nothing for do, then the PMS lib will use the Google Awareness.
However, it's possible to use several other IoT middleware just implementing a class of type `IoTMiddlewareAdapter`. See the [example](https://github.com/makleyston-ufc/PMS/blob/main/PMSlib/app/src/main/java/br/ufc/mdcc/cmu/pmslib/iotmiddleware/googleawareness/IoTMiddlewareAdapterImpl.java) to implement a IoT middleware adapter. 

Implement the listener to receive the IoT middleware data. See the [example](https://github.com/makleyston-ufc/PMS/blob/main/PMSlib/app/src/main/java/br/ufc/mdcc/cmu/pmslib/iotmiddleware/googleawareness/IoTMiddlewareListenerImpl.java) to implement a IoT middleware listener.

If you implemented a new IoT middleware adapter, then you must to set it in the PMS lib. See the example follwing:
```
PMSInterface pms = PMS.getInstance(this);

/*Create an instance of type IoTMiddlewareAdapter that you have deployed*/
IoTMiddlewareAdapterImpl ioTMiddlewareAdapter = new IoTMiddlewareAdapterImpl(this);

/*Create an instance of type IoTMiddlewareListener that you have deployed*/
IoTMiddlewareListenerImpl ioTMiddlewareListener = new IoTMiddlewareListenerImpl(this);

/*Setting the listener in the IoT middleware adapter*/
ioTMiddlewareAdapter.setIoTMiddlewareListener(ioTMiddlewareListener);

/*Setting the IoT middleware adapter in the PMS lib*/
pms.setIoTMiddlewareAdapter(ioTMiddlewareAdapter);

try {
    pms.start();
} catch (PMSException e) {
    e.printStackTrace();
}
```

#### 4. How to consume the data that PMS lib publishes?
Implement a MQTT subscriber your preference to comsume the data published by PMS lib in the MQTT Broker. We suggest the MQTT Client [Paho](https://www.eclipse.org/paho/). 
PMS lib publishes an RDF file in the MQTT Broker ever that the CEP engine detects an event. So, your client app must read these messages. See a message example of GPS semantic data published in the MQTT Broker following.
```
<?xml version="1.0" encoding="UTF-8"?>
    <rdf:RDF
        xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
        xmlns:owl="http://www.w3.org/2002/07/owl#"
        xmlns:geo="http://www.w3.org/2003/01/geo/wgs84_pos/"
        xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
        xmlns:iot-lite="http://purl.oclc.org/NET/UNIS/fiware/iot-lite/"
        xmlns:ssn="http://purl.oclc.org/NET/ssnx/ssn/"
        xmlns:xsd="http://www.w3.org/2001/XMLSchema#" > 
      <rdf:Description rdf:about="http://www.w3.org/2003/01/geo/wgs84_pos/Point">
        <geo:long rdf:datatype="http://www.w3.org/2001/XMLSchema#double">XXXXXXXXX</geo:long>
        <geo:lat rdf:datatype="http://www.w3.org/2001/XMLSchema#double">YYYYYYYY</geo:lat>
        <rdf:type rdf:resource="http://www.w3.org/2003/01/geo/wgs84_pos/LocalizationSensor"/>
      </rdf:Description>
      <rdf:Description rdf:about="http://purl.oclc.org/NET/UNIS/fiware/iot-lite/LocalizationSensorID123">
        <geo:location rdf:resource="http://www.w3.org/2003/01/geo/wgs84_pos/Point"/>
        <rdf:type rdf:resource="http://purl.oclc.org/NET/ssnx/ssn/LocationSensor"/>
      </rdf:Description>
    </rdf:RDF>
```

#### 5. How to publish RDF data to a web service using the RESTFul `POST` command? 
Create an instance of type `ConfigREST` and set it in the PMS lib. See the example following:
```
PMSInterface pms = PMS.getInstance(this);

/*Create an instance of type ConfigREST (with host and port value) and set it in the PMS lib*/
pms.setConfigREST(new ConfigREST("192.168.1.67"));

try {
    pms.start();
} catch (PMSException e) {
    e.printStackTrace();
}
```
The message contains an array of topics and a RDF content. This message is posts in the URI `/PMSPublish` of RESTFul service.



________________
Contributors:
* [Danne Makleyston Gomes Pereira](http://lattes.cnpq.br/2002489019346835)
* [Francisca Jamires da Costa](http://lattes.cnpq.br/0967947765723262)

This project originated in subject Ubiquitous and Mobile Computation of postgraduate course in computer science of University Federal of Ceara.

<img src="https://www.quixada.ufc.br/wp-content/Arquivos_Site/Brasao%20Vertical%20UFC%20Policromatico.png" alt="drawing" width="100"/>
