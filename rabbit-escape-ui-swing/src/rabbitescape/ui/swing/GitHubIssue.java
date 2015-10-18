package rabbitescape.ui.swing;

public class GitHubIssue
{
    private int number;
    private boolean isLevel;
    private boolean isBug;
    private String body; /*< @brief body text excluding world text. */
    private String title;

    public GitHubIssue( boolean isLevel, boolean isBug )
    {
        this.isLevel = isLevel;
        this.isBug = isBug;
    }

    public GitHubIssue()
    {
        // TODO Auto-generated constructor stub
    }

    public int getNumber()
    {
        return number;
    }

    public void setNumber( int number )
    {
        this.number = number;
    }

    public boolean isLevel()
    {
        return isLevel;
    }

    public void setLevel( boolean isLevel )
    {
        this.isLevel = isLevel;
    }

    public boolean isBug()
    {
        return isBug;
    }

    public void setBug( boolean isBug )
    {
        this.isBug = isBug;
    }

    public String getBody()
    {
        return body;
    }

    public void setBody( String body )
    {
        this.body = body;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }
}