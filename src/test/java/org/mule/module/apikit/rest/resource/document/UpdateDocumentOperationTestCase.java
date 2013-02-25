
package org.mule.module.apikit.rest.resource.document;

import org.mule.api.MuleException;
import org.mule.module.apikit.rest.operation.AsbtractOperationTestCase;
import org.mule.module.apikit.rest.operation.RestOperation;

import org.junit.Before;

public class UpdateDocumentOperationTestCase extends AsbtractOperationTestCase
{

    UpdateDocumentOperation operation;

    @Override
    public RestOperation getOperation()
    {
        return operation;
    }

    @Before
    public void setup() throws MuleException
    {
        super.setup();
        operation = new UpdateDocumentOperation();
        operation.setHandler(handler);
    }

}
