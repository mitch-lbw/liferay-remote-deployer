/**
 * 
 */
package de.usu.liferay.deploy;

/**
 * <strong>Projekt: deploy-maven-plugin</strong><br />
 * <strong>Klasse: de.usu.maven.liferay.deploy.SupportedTypes</strong><br />
 *
 * <hr noshade />
 * <strong>Beschreibung:</strong><br />
 * 
 * <br />
 * <hr noshade />
 * <strong>Historie:</strong><br />
 * 09.08.2016;usimmeier;Erstellung<br />
 *
 * <br />
 * <hr noshade />
 * 
 * @since 1.4
 * @author usimmeier
 * @version 1.0
 */
public enum SupportedTypes
{
    WAR("war"), EAR("ear");

    /**
     * 
     */
    private SupportedTypes( String postfix )
    {
        this.postfix = postfix;
    }

    private String postfix;

    /**
     * @return the postfix
     */
    public String getPostfix()
    {
        return postfix;
    }

    /**
     * @param postfix
     *            the postfix to set
     */
    public void setPostfix( String postfix )
    {
        this.postfix = postfix;
    }

    public static String printSupportedType()
    {
        StringBuilder sb = new StringBuilder( "Supported artifact types are" );
        for ( SupportedTypes type : SupportedTypes.values() )
        {
            sb.append( "\n" );
            sb.append( "\"" );
            sb.append( type.getPostfix() );
            sb.append( "\"" );
        }
        return sb.toString();
    }

    public static boolean isSupported( String type )
    {
        boolean isSupported = false;
        for ( SupportedTypes supportedType : SupportedTypes.values() )
        {
            if ( supportedType.getPostfix().equalsIgnoreCase( type ) )
            {
                isSupported = true;
                break;
            }
        }
        return isSupported;
    }
}
