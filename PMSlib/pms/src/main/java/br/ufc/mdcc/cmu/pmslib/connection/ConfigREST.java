package br.ufc.mdcc.cmu.pmslib.connection;

public class ConfigREST {
    private String host;
    private String port;
    private final String PMS_PUBLISH = "/PMSpublish";

    public ConfigREST(){}

    public ConfigREST(String host){
        this.host = host;
    }

    public ConfigREST(String host, String port){
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
