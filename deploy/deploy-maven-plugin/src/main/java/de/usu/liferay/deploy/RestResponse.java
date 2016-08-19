/**
 * 
 */
package de.usu.liferay.deploy;

import java.io.IOException;
import java.io.Serializable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;

/**
 * <strong>Projekt: deploy-maven-plugin</strong><br />
 * <strong>Klasse: de.usu.maven.liferay.deploy.HttpResponse</strong><br />
 *
 * <hr noshade />
 * <strong>Beschreibung:</strong><br />
 * 
 * <br />
 * <hr noshade />
 * <strong>Historie:</strong><br />
 * 07.08.2016;usimmeier;Erstellung<br />
 *
 * <br />
 * <hr noshade />
 * 
 * @since 1.4
 * @author usimmeier
 * @version 1.0
 */
public class RestResponse implements Serializable
{
    private static final long serialVersionUID = 5892051649366469296L;

    private int statusCode;

    private String reasonPhrase;

    /**
     * @param statusCode
     * @param reasonPhrase
     * @throws IOException 
     * @throws ParseException 
     */
    public RestResponse( HttpResponse response ) throws ParseException, IOException
    {
        super();
        StatusLine statusLine = response.getStatusLine();
        this.statusCode = statusLine.getStatusCode();
        this.reasonPhrase = statusLine.getReasonPhrase();
        HttpEntity resEntity = response.getEntity();
        if ( resEntity != null )
        {
            this.additionalInformation = EntityUtils.toString( resEntity );
        }
    }

    private String additionalInformation;

    public String getInformationString()
    {
        return reasonPhrase + " - " + additionalInformation;
    }

    /**
     * @return the statusCode
     */
    public int getStatusCode()
    {
        return statusCode;
    }

    /**
     * @param statusCode
     *            the statusCode to set
     */
    public void setStatusCode( int statusCode )
    {
        this.statusCode = statusCode;
    }

    /**
     * @param reasonPhrase
     *            the reasonPhrase to set
     */
    public void setReasonPhrase( String reasonPhrase )
    {
        this.reasonPhrase = reasonPhrase;
    }

    /**
     * @param additionalInformation
     *            the additionalInformation to set
     */
    public void setAdditionalInformation( String additionalInformation )
    {
        this.additionalInformation = additionalInformation;
    }

}
