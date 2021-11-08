/*******************************************************************************
 * Copyright (c) 2016 Condast and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Condast                - EetMee
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.churuata.digital.authentication;

//Lists the packages that are needed, but are not detected by dependency analysis
//The crypto packages are needed to run the bundle correctly on a Virgo server
import javax.annotation.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.crypto.interfaces.*;
import javax.security.auth.spi.*;
import org.glassfish.jersey.*;

import org.apache.commons.codec.*;

import org.eclipse.jface.*;
import org.eclipse.core.commands.*;
import org.eclipse.equinox.http.registry.*;

//Needs to start first
import org.eclipse.persistence.*;

//Indirect references
import javax.servlet.*;
import org.eclipse.equinox.security.auth.module.*;
import org.eclipse.jface.window.*;
import org.eclipse.rap.rwt.service.*;

import org.osgi.framework.*;
import org.osgi.service.component.annotations.*;
import org.osgi.service.prefs.*;

@SuppressWarnings("unused")
public interface IHiddenPackages {

}
