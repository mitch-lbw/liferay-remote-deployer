/**
 * 
 */
package de.usu.liferay.remote.service.impl;

import com.liferay.portal.kernel.deploy.DeployManagerUtil;
import com.liferay.portal.kernel.deploy.auto.context.AutoDeploymentContext;
import com.liferay.portal.kernel.plugin.PluginPackage;

import de.usu.liferay.remote.ReturnCode;
import de.usu.liferay.remote.service.DeployerService;

import java.io.File;

import javax.naming.NameNotFoundException;
import javax.naming.TimeLimitExceededException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * <strong>Projekt: liferay-remote-deployer</strong><br />
 * <strong>Klasse: de.usu.liferay.remote.DeployerServiceImpl</strong><br />
 *
 * Service class for deploying / undeploying
 * Exceptions will be handled in controller because of HTTP return codes
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
@Service
public class DeployerServiceImpl implements DeployerService
{
    public static final long DEFAULT_MAX_WAIT_TIME = 30000;

    public static final long WAIT_INTERVAL = 5000;

    public ReturnCode undeploy( final String pluginPackageName, final Long maxWaitTime, boolean ignoreNotDeployed ) throws Exception
    {
        PluginPackage toUninstall = DeployManagerUtil.getInstalledPluginPackage( pluginPackageName );

        if ( toUninstall != null )
        {
            DeployManagerUtil.undeploy( pluginPackageName );
            waitForUndeploy( pluginPackageName, getMaxWaitTime( maxWaitTime ) );
        }
        else
        {
            if ( !ignoreNotDeployed )
            {
                throw new NameNotFoundException();
            }
        }
        return ReturnCode.UNDEPLOY_OK;
    }

    public ReturnCode deploy( final String pluginPackageName, final MultipartFile file, final Long maxWaitTime ) throws Exception
    {
        AutoDeploymentContext ctx = new AutoDeploymentContext();
        File convFile = new File( file.getOriginalFilename() );
        file.transferTo( convFile );
        convFile.createNewFile();
        ctx.setFile( convFile );
        DeployManagerUtil.deploy( ctx );
        waitForDeploy( pluginPackageName, getMaxWaitTime( maxWaitTime ) );
        
        return ReturnCode.DEPLOY_OK;

    }

    private void waitForUndeploy( final String context, final long maxWaitTime ) throws InterruptedException, TimeLimitExceededException
    {
        boolean undeployed = false;
        long waitTime = 0L;
        while ( waitTime < maxWaitTime )
        {
            Thread.sleep( WAIT_INTERVAL );
            waitTime += WAIT_INTERVAL;
            if ( DeployManagerUtil.getInstalledPluginPackage( context ) == null )
            {
                undeployed = true;
                break;
            }
        }
        if ( !undeployed )
        {
            throw new TimeLimitExceededException( "undeploy" );
        }
    }

    private void waitForDeploy( final String context, final long maxWaitTime ) throws InterruptedException, TimeLimitExceededException
    {
        boolean deployed = false;
        long waitTime = 0L;
        while ( waitTime < maxWaitTime )
        {
            Thread.sleep( WAIT_INTERVAL );
            waitTime += WAIT_INTERVAL;
            if ( DeployManagerUtil.getInstalledPluginPackage( context ) != null )
            {
                deployed = true;
                break;
            }
        }
        if ( !deployed )
        {
            throw new TimeLimitExceededException( "Deploy" );
        }
    }

    private long getMaxWaitTime( final Long givenTime )
    {
        return givenTime != null ? givenTime : DEFAULT_MAX_WAIT_TIME;
    }

}
