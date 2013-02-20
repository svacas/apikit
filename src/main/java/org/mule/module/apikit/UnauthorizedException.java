/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.apikit;

import org.mule.module.apikit.api.WebServiceRoute;
import org.mule.module.apikit.rest.RestException;

public class UnauthorizedException extends RestException
{
    private static final long serialVersionUID = 7873091243309548834L;

    public UnauthorizedException(WebServiceRoute webServiceRoute)
    {
        // TODO Auto-generated constructor stub
    }

}