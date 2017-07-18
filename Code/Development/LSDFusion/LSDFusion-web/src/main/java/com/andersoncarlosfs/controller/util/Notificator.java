package com.andersoncarlosfs.controller.util;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public abstract class Notificator {

    /**
     *
     * @param severity
     * @param message
     * @param detail
     */
    private static void addMessage(FacesMessage.Severity severity, String message, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, message, detail));
    }
    
    /**
     *
     * @param message
     * @param detail
     */
    public static void addErrorMessage(String message, String detail) {
        addMessage(FacesMessage.SEVERITY_ERROR, message, detail);
    }

    /**
     *
     * @param message
     * @param detail
     */
    public static void addWarningMessage(String message, String detail) {
        addMessage(FacesMessage.SEVERITY_WARN, message, detail);
    }
    
    /**
     *
     * @param message
     * @param detail
     */
    public static void addSuccessMessage(String message, String detail) {
        addMessage(FacesMessage.SEVERITY_INFO, message, detail);
    }
    
    /**
     * 
     * @param message 
     * @param exception 
     */
    public static void log(String message, Throwable exception) {
        FacesContext.getCurrentInstance().getExternalContext().log(message, exception);
    }

}
