package rabbitescape.ui.android;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatButton;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import static net.artificialworlds.rabbitescape.BuildConfig.APPLICATION_ID;

public class AbilityButton extends AppCompatButton
{
    private class OnClickListener implements ImageButton.OnClickListener
    {
        @Override
        public void onClick( View view )
        {
            group.check( buttonIndex );
        }
    }

    private final RadioGroup group;

    private final String ability;
    private final int buttonIndex;
    private boolean checked;

    public AbilityButton(
        Context context,
        Resources resources,
        RadioGroup group,
        String ability,
        int numLeft,
        int buttonIndex
    )
    {
        super( context );
        this.group = group;
        this.ability = ability;
        this.buttonIndex = buttonIndex;
        this.checked = false;

        setUpLayout();
        setImage( resources );
        doSetNumLeft( numLeft );
        setOnClickListener( new OnClickListener() );
        setContentDescription( ability );
    }

    private void setUpLayout()
    {
        setLayoutParams(
            new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        );
    }

    private void setImage( Resources resources )
    {
        Drawable img = resources.getDrawable(
            resources.getIdentifier(
                "ability_" + ability,
                "drawable",
                APPLICATION_ID
            )
        );

        setTextSize( TypedValue.COMPLEX_UNIT_SP, 10 );

        setCompoundDrawablesWithIntrinsicBounds(
            img,
            null,
            null,
            null
        );
    }

    public void disable()
    {
        super.setEnabled( false );
        setMinimumWidth( getWidth() );
        setMinimumHeight( getHeight() );
        setCompoundDrawablesWithIntrinsicBounds( null, null, null, null );
        invalidate();
    }

    public void setChecked( boolean checked )
    {
        this.checked = checked;

        if ( checked )
        {
            getBackground().setColorFilter( 0x770000FF, PorterDuff.Mode.SRC_ATOP );
        }
        else
        {
            getBackground().clearColorFilter();
        }
        invalidate();
    }

    public boolean isChecked()
    {
        return checked;
    }

    public String ability()
    {
        return ability;
    }

    public void setNumLeft( int numLeft )
    {
        setMinimumWidth( getWidth() );
        setMinimumHeight( getHeight() );
        doSetNumLeft( numLeft );
    }

    private void doSetNumLeft( int numLeft )
    {
        String text = String.valueOf( numLeft );
        if ( text.length() < 2 )
        {
            text = " " + text;
        }
        setText( text );
    }
}
