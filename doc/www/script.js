
var uparrow;
var downarrow;

function scroll()
{
    if ( !uparrow )
    {
        return;
    }

    if( window.pageYOffset === 0 )
    {
        uparrow.style.visibility = "hidden";
    }
    else
    {
        uparrow.style.visibility = "visible";
    }

    if ( window.scrollMaxY - window.pageYOffset < 5 )
    {
        downarrow.style.visibility = "hidden";
    }
    else
    {
        downarrow.style.visibility = "visible";
    }
}

function doScroll( direction )
{
    window.scrollBy( 0, direction * 0.5 * window.innerHeight );
}

function load()
{
    uparrow   = document.getElementById( "uparrow" );
    downarrow = document.getElementById( "downarrow" );

    uparrow.onclick   = function() { doScroll( -1 ) };
    downarrow.onclick = function() { doScroll(  1 ) };

    scroll();
}

window.onload = load;
window.onscroll = scroll;

