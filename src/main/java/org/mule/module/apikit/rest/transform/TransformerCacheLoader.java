package org.mule.module.apikit.rest.transform;

import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.transformer.DataType;
import org.mule.api.transformer.Transformer;
import org.mule.module.xml.transformer.jaxb.JAXBMarshallerTransformer;
import org.mule.module.xml.transformer.jaxb.JAXBUnmarshallerTransformer;
import org.mule.transformer.types.MimeTypes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheLoader;
import com.sun.xml.bind.api.JAXBRIContext;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

public class TransformerCacheLoader extends CacheLoader<DataTypePair, Transformer>
{

    private static final Logger LOGGER = Logger.getLogger(TransformerCacheLoader.class);
    private final MuleContext muleContext;

    public TransformerCacheLoader(MuleContext muleContext)
    {
        this.muleContext = muleContext;
    }

    @Override
    public Transformer load(DataTypePair dataTypePair) throws Exception
    {
        return resolveTransformer(muleContext, dataTypePair.getSourceDataType(), dataTypePair.getResultDataType());
    }

    protected Transformer resolveTransformer(MuleContext muleContext, DataType sourceDataType, DataType resultDataType) throws MuleException
    {
        if (sourceDataType.getMimeType().equals(MimeTypes.JSON) || sourceDataType.getMimeType().endsWith("+json"))
        {
            JacksonToObject jto = new JacksonToObject();
            jto.setReturnDataType(resultDataType);
            jto.setMapper(new ObjectMapper());
            muleContext.getRegistry().applyProcessorsAndLifecycle(jto);
            return jto;
        }
        else if (resultDataType.getMimeType().equals(MimeTypes.JSON) || resultDataType.getMimeType().endsWith("+json"))
        {
            ObjectToJackson otj = new ObjectToJackson();
            otj.setSourceClass(sourceDataType.getType());
            otj.setReturnDataType(resultDataType);
            otj.setMapper(new ObjectMapper());
            muleContext.getRegistry().applyProcessorsAndLifecycle(otj);
            return otj;
        }
        else if (sourceDataType.getMimeType().equals(MimeTypes.XML) || sourceDataType.getMimeType().endsWith("+xml"))
        {
            try
            {
                JAXBUnmarshallerTransformer jmt = new JAXBUnmarshallerTransformer(JAXBContext.newInstance(resultDataType.getType()), resultDataType);
                muleContext.getRegistry().applyProcessorsAndLifecycle(jmt);
                return jmt;
            }
            catch (JAXBException e)
            {
                LOGGER.error("Unable to create JAXB unmarshaller for " + resultDataType, e);
            }
        }
        else if (resultDataType.getMimeType().equals(MimeTypes.XML) || resultDataType.getMimeType().endsWith("+xml"))
        {
            try
            {
                TransientAnnotationReader reader = new TransientAnnotationReader();
                reader.addTransientField(Throwable.class.getDeclaredField("stackTrace"));
                reader.addTransientMethod(Throwable.class.getDeclaredMethod("getStackTrace"));

                Map<String, Object> jaxbConfig = new HashMap<String, Object>();
                jaxbConfig.put(JAXBRIContext.ANNOTATION_READER, reader);

                JAXBContext jaxbContext = JAXBContext.newInstance(new Class[] {sourceDataType.getType()}, jaxbConfig);
                JAXBMarshallerTransformer jut = new JAXBMarshallerTransformer(jaxbContext, resultDataType);
                jut.setSourceClass(sourceDataType.getType());
                muleContext.getRegistry().applyProcessorsAndLifecycle(jut);
                return jut;
            }
            catch (JAXBException e)
            {
                LOGGER.error("Unable to create JAXB marshaller for " + resultDataType, e);
            }
            catch (NoSuchMethodException e)
            {
                LOGGER.error("Unable to create JAXB marshaller for " + resultDataType, e);
            }
            catch (NoSuchFieldException e)
            {
                LOGGER.error("Unable to create JAXB marshaller for " + resultDataType, e);
            }

        }

        return muleContext.getRegistry().lookupTransformer(sourceDataType, resultDataType);
    }

}