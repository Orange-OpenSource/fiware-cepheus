package com.orange.espr4fastdata.model.cep;

/**
 * Created by pborscia on 03/06/2015.
 */
public class Attribute {
    private String name;
    private String type;

    public Attribute() {
    }

    public Attribute(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean equals(Attribute a) {
        return name.equals(a.name) && type.equals(a.type);
    }
}
