/**
 * 
 */
package de.usu.liferay.remote.service;

import org.springframework.web.multipart.MultipartFile;

import de.usu.liferay.remote.ReturnCode;

/**
 * <strong>Projekt: liferay-remote-deployer</strong><br />
 * <strong>Klasse: de.usu.liferay.remote.service.DeployerService</strong><br />
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
public interface DeployerService
{

    /**
     * @param pluginPackageName
     *            name of the pluginPackage to deploy
     * @param maxWaitTime
     *            max time to wait for deploy success
     * @param ignoreNotDeployed
     *            if true, when pluginPackage is not found no exception will be
     *            thrown
     * @return HTTP Return Code with explanation
     * @throws Exception
     */
    public ReturnCode undeploy( String pluginPackageName, Long maxWaitTime, boolean ignoreNotDeployed ) throws Exception;

    /**
     * @param pluginPackageName
     * @param file
     * @param maxWaitTime
     * @return
     * @throws Exception
     */
    public ReturnCode deploy( String pluginPackageName, MultipartFile file, Long maxWaitTime ) throws Exception;

}
