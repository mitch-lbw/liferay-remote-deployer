/**
 * 
 */
package de.usu.liferay.remote.controller;

import com.liferay.portal.kernel.deploy.DeployManagerUtil;
import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.kernel.util.StringPool;

import de.usu.liferay.remote.ReturnCode;
import de.usu.liferay.remote.service.DeployerService;

import javax.naming.NameNotFoundException;
import javax.naming.TimeLimitExceededException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

/**
 * <strong>Projekt: liferay-remote-deployer</strong><br />
 * <strong>Klasse: de.usu.liferay.remote.RemoteDeployerController</strong><br />
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
@Controller
public class RemoteDeployerController
{
    public static final long MAX_WAIT_TIME = 5000;

    public static final long WAIT_INTERVAL = 1500;

    @Autowired
    private DeployerService deployerService;

    @RequestMapping( value = "/undeploy/{name}/{maxWaitTime}", method = RequestMethod.GET )
    @ResponseStatus( code = HttpStatus.OK )
    public @ResponseBody String undeploy( @PathVariable( "name" ) String mavenPluginName, @PathVariable( "maxWaitTime" ) Long maxWaitTime ) throws Exception
    {
        ReturnCode returnCode = this.deployerService.undeploy( mavenPluginName, maxWaitTime, false );
        return returnCode.getMessage();
    }

    @RequestMapping( value = "/undeploy/{name}", method = RequestMethod.GET )
    @ResponseStatus( code = HttpStatus.OK )
    public @ResponseBody String undeployDefault( @PathVariable( "name" ) String mavenPluginName ) throws Exception
    {
        ReturnCode returnCode = this.deployerService.undeploy( mavenPluginName, null, false );
        return returnCode.getMessage();
    }

    @RequestMapping( value = "/deploy/{name}/{maxWaitTime}", method = RequestMethod.POST )
    public @ResponseBody String deploy( @PathVariable( "file" ) MultipartFile file, @PathVariable( "name" ) String mavenPluginName,
                                        @PathVariable( "maxWaitTime" ) Long maxWaitTime ) throws Exception
    {
        ReturnCode returnCode = this.deployerService.deploy( mavenPluginName, file, maxWaitTime );
        return returnCode.getMessage();
    }

    @RequestMapping( value = "/deploy/{name}", method = RequestMethod.POST )
    public @ResponseBody String deployDefault( @PathVariable( "file" ) MultipartFile file, @PathVariable( "name" ) String mavenPluginName ) throws Exception
    {
        ReturnCode returnCode = this.deployerService.deploy( mavenPluginName, file, null );
        return returnCode.getMessage();
    }

    @RequestMapping( value = "/cleanDeploy/{name}/{maxWaitTime}", method = RequestMethod.POST )
    public @ResponseBody String cleanDeploy( @PathVariable( "file" ) MultipartFile file, @PathVariable( "name" ) String mavenPluginName,
                                             @PathVariable( "maxWaitTime" ) Long maxWaitTime ) throws Exception
    {
        return doCleanDeploy( mavenPluginName, file, maxWaitTime );
    }

    @RequestMapping( value = "/cleanDeploy/{name}", method = RequestMethod.POST )
    public @ResponseBody String cleanDeployDefault( @PathVariable( "file" ) MultipartFile file, @PathVariable( "name" ) String mavenPluginName )
    throws Exception
    {
        return doCleanDeploy( mavenPluginName, file, null );
    }

    private String doCleanDeploy( final String mavenPluginName, final MultipartFile file, final Long maxWaitTime ) throws Exception
    {
        ReturnCode returnCode;
        returnCode = this.deployerService.undeploy( mavenPluginName, maxWaitTime, true );
        if ( returnCode == ReturnCode.UNDEPLOY_OK )
        {
            returnCode = this.deployerService.deploy( mavenPluginName, file, maxWaitTime );
        }
        return returnCode.getMessage();
    }

    @RequestMapping( value = "/getpackages", method = RequestMethod.GET )
    public @ResponseBody String getAvailablePackages()
    {
        StringBuilder sb = new StringBuilder();
        sb.append( "Available portlet names are" );
        sb.append( StringPool.NEW_LINE );
        for ( PluginPackage pack : DeployManagerUtil.getInstalledPluginPackages() )
        {
            sb.append( "Name: " );
            sb.append( pack.getName() );
            sb.append( StringPool.SPACE );
            sb.append( "Context: " );
            sb.append( pack.getContext() );
            sb.append( StringPool.NEW_LINE );
        }
        return sb.toString();
    }

    @ResponseStatus( code = HttpStatus.REQUEST_TIMEOUT )
    @ExceptionHandler( value = TimeLimitExceededException.class )
    public @ResponseBody String handleTimeLimitException( TimeLimitExceededException e )
    {
        if ( "undeploy".equals( e.getMessage() ) )
            return ReturnCode.UNDEPLOY_TIMEOUT.getMessage();
        else
            return ReturnCode.DEPLOY_TIMEOUT.getMessage();
    }

    @ResponseStatus( code = HttpStatus.BAD_REQUEST )
    @ExceptionHandler( value = NameNotFoundException.class )
    public @ResponseBody String handleNameNotFoundException( NameNotFoundException e )
    {
        return ReturnCode.UNDEPLOY_NAME_NOT_FOUND.getMessage();
    }

    @ResponseStatus( code = HttpStatus.INTERNAL_SERVER_ERROR )
    @ExceptionHandler( value = Exception.class )
    public @ResponseBody String handleException( Exception e )
    {
        return ReturnCode.UNEXPECTED_EXCEPTION.getMessage();
    }

}
