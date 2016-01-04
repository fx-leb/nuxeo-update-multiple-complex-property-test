/*
 * Copyright (c) 2016 by KEENDO
 * Project : keendo-retail-core-common
 * File : ComplexMultiplePropertyTest.java
 * Created on 4 janv. 2016 by fxl
 */
package org.nuxeo.sample.test;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;


/**
 * Checks update of multiple complex sub property item. The sub property item is a multiple one.
 *
 * @author fxl
 *
 */
@RunWith(FeaturesRunner.class)
@Features(ComplexMultiplePropertyFeature.class)
public class ComplexMultiplePropertyTest {

    /** The test document name. */
    public static final String SPECIFICATIONS_DOC_NAME         = "specifications1";

    /** A multiple complex property. */
    public static final String COMPLEX_MULTIPLE_PROPERTY_XPATH = "ks:exhaustiveComposing";

    /** A sub property of {@link #COMPLEX_MULTIPLE_PROPERTY_XPATH}. It's a multiple sub property */
    public static final String MULTIPLE_SUB_PROPERTY_XPATH     = "inducedAllergenPresence";

    /** Nuxeo document manager. */
    @Inject
    protected CoreSession      coreSession;

    /**
     * The sub property item value is updated.
     */
    @Test
    public void updateSubPropertyValue() {
        genericTest(new String[] { "valeur1" }, new String[] { "valeur2" });
    }

    @Test
    public void eraseSubPropertyValue() {
        genericTest(new String[] { "valeur1" }, null);
    }

    @Test
    public void setSubPropertyToEmpty() {
        genericTest(new String[] { "valeur1" }, new String[] {});
    }

    @Test
    public void setSubPropertyToBlankValue() {
        genericTest(new String[] { "valeur1" }, new String[] { "" });
    }

    @SuppressWarnings("unchecked")
    private void genericTest(final String[] initialValue, final String[] newValue) {
        createAndInitializeTestDocument(initialValue, SPECIFICATIONS_DOC_NAME);

        // get the document to test
        final DocumentModel document = coreSession.getDocument(new PathRef(coreSession.getRootDocument().getPathAsString(), SPECIFICATIONS_DOC_NAME));

        updatePropertyValue(newValue, document);

        // reload the document
        final DocumentModel reloadedDocument = coreSession.getDocument(document.getRef());

        final List<Map<String, Serializable>> complexMultiplePropertyReloaded = (List<Map<String, Serializable>>) reloadedDocument
                .getPropertyValue(COMPLEX_MULTIPLE_PROPERTY_XPATH);
        final String[] multiplePropReloaded = (String[]) complexMultiplePropertyReloaded.get(0).get(MULTIPLE_SUB_PROPERTY_XPATH);

        // compare the expected value to the
        boolean ko;
        if (newValue == null || newValue.length == 0) {
            ko = multiplePropReloaded != null && multiplePropReloaded.length > 0;
        } else if (newValue.length == multiplePropReloaded.length) {
            ko = false;
            for (int i = 0; i < newValue.length; i++) {
                if (!StringUtils.equals(newValue[i], multiplePropReloaded[i])) {
                    ko = true;
                    break;
                }
            }
        } else {
            ko = true;
        }
        Assert.assertFalse(ko);
    }

    /**
     * Updates the sub multiple property of the complex multiple property value.
     *
     * @param newValue
     * @param document
     */
    private void updatePropertyValue(final String[] newValue, final DocumentModel document) {
        final List<Map<String, Serializable>> multipleComplexProperty = new ArrayList<>();
        final Map<String, Serializable> multipleComplexPropertyItem = new HashMap<>();
        multipleComplexPropertyItem.put(MULTIPLE_SUB_PROPERTY_XPATH, newValue);
        multipleComplexProperty.add(multipleComplexPropertyItem);

        document.setPropertyValue(COMPLEX_MULTIPLE_PROPERTY_XPATH, (Serializable) multipleComplexProperty);
        coreSession.saveDocument(document);
    }

    /**
     * Creates a test document and initializes the complex multiple property value.
     *
     * @param initialValue the multiple sub property
     * @param docName
     */
    private void createAndInitializeTestDocument(final String[] initialValue, final String docName) {
        DocumentModel doc = coreSession.createDocumentModel(coreSession.getRootDocument().getPathAsString(), docName, "kSpecifications");
        doc.setProperty("dublincore", "title", docName);

        final List<Map<String, Serializable>> exhaustiveComposing = new ArrayList<>();
        final Map<String, Serializable> exhaustiveComposingElt = new HashMap<>();
        exhaustiveComposingElt.put(MULTIPLE_SUB_PROPERTY_XPATH, initialValue);
        exhaustiveComposing.add(exhaustiveComposingElt);
        doc.setPropertyValue(COMPLEX_MULTIPLE_PROPERTY_XPATH, (Serializable) exhaustiveComposing);
        doc = coreSession.createDocument(doc);
    }

    /**
     * Reset the test data set after test running.
     *
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        coreSession.removeChildren(coreSession.getRootDocument().getRef());
        coreSession.save();
    }

}
