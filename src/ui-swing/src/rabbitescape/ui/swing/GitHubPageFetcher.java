package rabbitescape.ui.swing;

public interface GitHubPageFetcher
{
    /**
     * The UI implements this to inform the user, and fetch the page.
     */
    public void notifyAndFetch( String url,
                                String requestProperty,
                                String notification,
        GitHubPageFetchNotifier notifier );
}
