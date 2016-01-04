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


@RunWith(FeaturesRunner.class)
@Features(ComplexMultiplePropertyFeature.class)
public class ComplexMultiplePropertyTest {

    public static final String SPECIFICATIONS_1_DOC_NAME       = "specifications1";
    public static final String SPECIFICATIONS_2_DOC_NAME       = "specifications2";

    public static final String MULTIPLE_SUB_PROPERTY_XPATH     = "inducedAllergenPresence";
    /** Multiple complex property. */
    public static final String COMPLEX_MULTIPLE_PROPERTY_XPATH = "ks:exhaustiveComposing";

    /** Nuxeo document manager. */
    @Inject
    protected CoreSession      coreSession;

    @Test
    public void test() {

        genericTest(new String[] { "valeur1" }, new String[] { "valeur2" });

    }

    @Test
    public void test2() {

        genericTest(new String[] { "valeur1" }, null);

    }

    @Test
    public void test3() {

        genericTest(new String[] { "valeur1" }, new String[] {});

    }

    @Test
    public void test4() {

        genericTest(new String[] { "valeur1" }, new String[] { "" });

    }

    @SuppressWarnings("unchecked")
    private void genericTest(final String[] initialValue, final String[] newValue) {
        createTestDataSet(initialValue, SPECIFICATIONS_1_DOC_NAME);

        // get the document to test
        final DocumentModel document = coreSession.getDocument(new PathRef(coreSession.getRootDocument().getPathAsString(), SPECIFICATIONS_1_DOC_NAME));

        final List<Map<String, Serializable>> exhaustiveComposing = new ArrayList<>();
        final Map<String, Serializable> exhaustiveComposingElt = new HashMap<>();
        exhaustiveComposingElt.put(MULTIPLE_SUB_PROPERTY_XPATH, newValue);
        exhaustiveComposing.add(exhaustiveComposingElt);

        document.setPropertyValue(COMPLEX_MULTIPLE_PROPERTY_XPATH, (Serializable) exhaustiveComposing);
        coreSession.saveDocument(document);

        final DocumentModel document2 = coreSession.getDocument(document.getRef());

        final List<Map<String, Serializable>> complexMultiplePropertyReloaded = (List<Map<String, Serializable>>) document2.getPropertyValue(COMPLEX_MULTIPLE_PROPERTY_XPATH);
        final String[] multiplePropReloaded = (String[]) complexMultiplePropertyReloaded.get(0).get(MULTIPLE_SUB_PROPERTY_XPATH);
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

    private void createTestDataSet(final String[] initialValue, final String specName) {
        DocumentModel doc = coreSession.createDocumentModel(coreSession.getRootDocument().getPathAsString(), specName, "kSpecifications");
        doc.setProperty("dublincore", "title", specName);

        final List<Map<String, Serializable>> exhaustiveComposing = new ArrayList<>();
        final Map<String, Serializable> exhaustiveComposingElt = new HashMap<>();
        exhaustiveComposingElt.put(MULTIPLE_SUB_PROPERTY_XPATH, initialValue);
        exhaustiveComposing.add(exhaustiveComposingElt);
        doc.setPropertyValue(COMPLEX_MULTIPLE_PROPERTY_XPATH, (Serializable) exhaustiveComposing);
        doc = coreSession.createDocument(doc);
    }

    /**
     * Remise à zéro de la base après le test.
     *
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        coreSession.removeChildren(coreSession.getRootDocument().getRef());
        coreSession.save();
    }

}
