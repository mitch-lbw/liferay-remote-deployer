/**
 * 
 */
package de.usu.liferay.deploy.maven;

import de.usu.liferay.deploy.RestResponse;
import de.usu.liferay.deploy.SupportedTypes;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * <strong>Projekt: deploy-maven-plugin</strong><br />
 * <strong>Klasse: de.usu.maven.liferay.deploy.DeployMojo</strong><br />
 *
 * <hr noshade />
 * <strong>Beschreibung:</strong><br />
 * 
 * <br />
 * <hr noshade />
 * <strong>Historie:</strong><br />
 * 06.08.2016;usimmeier;Erstellung<br />
 *
 * <br />
 * <hr noshade />
 * 
 * @since 1.4
 * @author usimmeier
 * @version 1.0
 */
@Mojo( name = "cleanDeploy" )
public class DeployMojo extends AbstractMojo
{

    @Parameter( defaultValue = "${project}", required = true, readonly = true )
    private MavenProject project;

    @Parameter( required = true )
    private String targetHost;

    @Parameter( required = false, defaultValue = "liferay-remote-deployer" )
    private String remoteDeployerPortletName;

    @Parameter( required = false, defaultValue = "services/cleanDeploy" )
    private String remoteDeployerPortletOperation;

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        getLog().info( "Starting deploy plugin" );

        String type = project.getArtifact().getType();
        if ( SupportedTypes.isSupported( type ) )
        {
            File targetFile = project.getArtifact().getFile();
            String pluginPackageName = project.getName();
            getLog().info( "Try to deploy target file " + targetFile.getAbsolutePath() + " with pluginContext name " + pluginPackageName );
            RestResponse restResponse = executeRestCall( targetFile, pluginPackageName );
            getLog().info( restResponse.getInformationString() );
            if ( restResponse.getStatusCode() != HttpStatus.SC_OK )
            {
                throw new MojoExecutionException( "Error occured deploying artifact " + restResponse.getInformationString() );
            }

        }
        else
        {
            throw new MojoExecutionException( SupportedTypes.printSupportedType() );
        }
    }

    private RestResponse executeRestCall( final File targetFile, final String pluginPackageName ) throws MojoExecutionException
    {
        HttpPost httppost = new HttpPost();
        try
        {
            HttpClient httpclient = new DefaultHttpClient();
            httpclient.getParams().setParameter( CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1 );

            httppost = new HttpPost( buildPostURL( targetFile.getName(), pluginPackageName ) );

            MultipartEntity mpEntity = new MultipartEntity();
            ContentBody contentBodyFile = new FileBody( targetFile, "multipart/form-data" );
            mpEntity.addPart( "file", contentBodyFile );

            httppost.setEntity( mpEntity );
            getLog().debug( "Executing HTTP call " + httppost.getRequestLine() );
            HttpResponse response = httpclient.execute( httppost );

            RestResponse restResponse = new RestResponse( response );

            getLog().debug( "Response from targetHost:" + response.getStatusLine() );

            httpclient.getConnectionManager().shutdown();

            return restResponse;
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Exception occured executing HTTP call to targetHost: " + httppost.getURI().toString(), e );
        }
    }

    private String buildPostURL( final String targetFileName, final String pluginPackageName )
    {
        StringBuilder postUrl = new StringBuilder( targetHost );
        if ( !targetHost.endsWith( "/" ) )
        {
            postUrl.append( "/" );
        }

        postUrl.append( remoteDeployerPortletName );
        if ( !remoteDeployerPortletOperation.startsWith( "/" ) )
        {
            postUrl.append( "/" );
        }

        postUrl.append( remoteDeployerPortletOperation );

        if ( !remoteDeployerPortletOperation.endsWith( "/" ) )
        {
            postUrl.append( "/" );
        }

        postUrl.append( pluginPackageName );

        return postUrl.toString();
    }
}
