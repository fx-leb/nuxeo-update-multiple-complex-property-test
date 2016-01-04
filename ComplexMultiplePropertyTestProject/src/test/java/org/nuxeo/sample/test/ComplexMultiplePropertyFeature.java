/*
 * Copyright (c) 2014 by KEENDO
 * Project : keendo-retail-test
 * File : ComplexMultiplePropertyFeature.java
 * Created on 21 mai 2015 by fxl
 */

package org.nuxeo.sample.test;


import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.RuntimeFeature;


/**
 * The Keendo core feature provides deployments needed to have a nuxeo core running. Several annotations can be used:
 *
 * @author fxl
 */
@Deploy({ "studio.extensions.kretail", })
@Features(RuntimeFeature.class)
public class ComplexMultiplePropertyFeature extends CoreFeature {

}
