/**
 * 
 */
package de.usu.liferay.remote;

/**
 * <strong>Projekt: liferay-remote-deployer</strong><br />
 * <strong>Klasse: de.usu.liferay.remote.ReturnCode</strong><br />
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
public enum ReturnCode
{
    UNDEPLOY_OK("Successfully undeployed package"), 
    DEPLOY_OK("Successfully deployed package"),
    UNDEPLOY_TIMEOUT("Max wait limit exceeded while waiting for package to undeploy"), 
    DEPLOY_TIMEOUT("Max wait limit exceeded while waiting for package to deploy"),
    UNEXPECTED_EXCEPTION("Unexpected error occured while undeploying package"), 
    UNDEPLOY_NAME_NOT_FOUND("Package to undeploy not found");

    private String message;

    /**
     * 
     */
    private ReturnCode( final String message )
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }
}
