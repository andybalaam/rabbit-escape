package rabbitescape.ui.android;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioGroup;

public class AbilityButton extends ImageButton
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
        Context context, Resources resources, RadioGroup group, String ability, int buttonIndex )
    {
        super( context );
        this.group = group;
        this.ability = ability;
        this.buttonIndex = buttonIndex;
        this.checked = false;

        setImage( resources );
        setUpLayout();
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
        setImageDrawable(
            resources.getDrawable(
                resources.getIdentifier(
                    "ability_" + ability,
                    "drawable",
                    "rabbitescape.ui.android"
                )
            )
        );
    }

    public void disable()
    {
        super.setEnabled( false );
        setMinimumWidth( getWidth() );
        setMinimumHeight( getHeight() );
        setImageDrawable( null );
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
}
