/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fhirjavaclientclosuretest;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import org.hl7.fhir.r4.model.*;

import ca.uhn.fhir.rest.client.impl.*;
import ca.uhn.fhir.context.*;
import ca.uhn.fhir.rest.api.*;
import ca.uhn.fhir.rest.client.method.*;
import ca.uhn.fhir.rest.gclient.*;
import org.hl7.fhir.instance.model.api.*;
import org.hl7.fhir.r4.*;

import java.util.List;
import org.hl7.fhir.r4.model.ConceptMap;
import org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupComponent;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ConceptMap.SourceElementComponent;

/**
 *
 * @author rhausam
 */
public class FHIRJavaClientClosureTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // create the FHIR context
        FhirContext ctx = FhirContext.forR4();
        String serverBase = "https://r4.ontoserver.csiro.au/fhir";

        IGenericClient client = ctx.newRestfulGenericClient(serverBase);

        // create the input parameters to pass to the server
        Parameters inParams = new Parameters();
        inParams.addParameter().setName("name").setValue(new StringType("closureTest"));
        // this should handle multiple concept parameters, but that doesn't seem to be working
        for (String arg: args) {
            inParams.addParameter().setName("concept").setValue(new Coding("http://snomed.info/sct", arg, null));
        }
        
        // initialize the output parameters
        Parameters outParams;
        // invoke the $closure operation
        String output;
        outParams = client
                .operation()
                .onServer()
                .named("$closure")
                .withParameters(inParams)
                .execute();
        
        // retrieve the returned ConceptMap resource
        ConceptMap outCM = (ConceptMap) outParams.getParameter().get(0).getResource();
        // get and print the mapping rows
        List<ConceptMapGroupComponent> maplist = outCM.getGroup();
        System.out.println();
        System.out.println("Source Code" + "\t" + "Target Code");
        for (ConceptMapGroupComponent cmgc: maplist) {
            for (SourceElementComponent src: cmgc.getElement()) {
                System.out.println(src.getCode() + "\t" + src.getTargetFirstRep().getCode());
            }
        }
        System.out.println();
    } 
}
