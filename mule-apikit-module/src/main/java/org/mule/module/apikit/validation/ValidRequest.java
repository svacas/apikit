/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.apikit.validation;

import org.mule.module.apikit.attributes.ApikitRequestAttributes;

public class ValidRequest {
  ApikitRequestAttributes attributes;
  ValidBody body;

  ValidRequest(ApikitRequestAttributes attributes, ValidBody body) {
    this.attributes = attributes;
    this.body = body;
  }

  public ApikitRequestAttributes getAttributes() {
    return attributes;
  }

  public ValidBody getBody() {
    return body;
  }

  public static ValidRequestBuilder builder() {
    return new ValidRequestBuilder();
  }

  static public class ValidRequestBuilder {

    ApikitRequestAttributes attributes;
    ValidBody body;

    public ValidRequestBuilder withAttributes(ApikitRequestAttributes attributes) {
      this.attributes = attributes;
      return this;
    }

    public ValidRequestBuilder withBody(ValidBody body) {
      this.body = body;
      return this;
    }

    public ValidRequest build() {
      return new ValidRequest(attributes, body);
    }
  }
}
