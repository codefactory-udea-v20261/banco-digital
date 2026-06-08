package com.udea.bancodigital.bdd.steps;

public class ScenarioContext {
    private Exception excepcion;

    public void setExcepcion(Exception e) {
        this.excepcion = e;
    }

    public Exception getExcepcion() {
        return excepcion;
    }

    public void reset() {
        this.excepcion = null;
    }

}
