package com.mattprecious.swirl;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import androidx.annotation.DrawableRes;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

public final class SwirlView extends ImageView {
    private State state = State.OFF;

    private Integer normalColor;
    private Integer errorColor;

    public SwirlView(Context context) {
        super(context);
    }

    @DrawableRes
    private static int getDrawable(State currentState, State newState, boolean animate) {
        switch (newState) {
            case OFF:
                if (animate) {
                    if (currentState == State.ON) {
                        return R.drawable.swirl_fingerprint_draw_off_animation;
                    } else if (currentState == State.ERROR) {
                        return R.drawable.swirl_error_draw_off_animation;
                    }
                }

                return 0;
            case ON:
                if (animate) {
                    if (currentState == State.OFF) {
                        return R.drawable.swirl_fingerprint_draw_on_animation;
                    } else if (currentState == State.ERROR) {
                        return R.drawable.swirl_fingerprint_error_state_to_fp_animation;
                    }
                }

                return R.drawable.swirl_fingerprint;
            case ERROR:
                if (animate) {
                    if (currentState == State.ON) {
                        return R.drawable.swirl_fingerprint_fp_to_error_state_animation;
                    } else if (currentState == State.OFF) {
                        return R.drawable.swirl_error_draw_on_animation;
                    }
                }
                return R.drawable.swirl_error;
            default:
                throw new IllegalArgumentException("Unknown state: " + newState);
        }
    }

    public void setState(State state) {
        setState(state, true);
    }

    public void setState(State state, boolean animate) {
        if (state == this.state) return;

        @DrawableRes int resId = getDrawable(this.state, state, animate);
        if (resId == 0) {
            setImageDrawable(null);
        } else {
            Drawable icon = null;
            if (animate) {
                icon = AnimatedVectorDrawableCompat.create(getContext(), resId);
            }
            if (icon == null) {
                icon = VectorDrawableCompat.create(getResources(), resId, getContext().getTheme());
            }
            // TODO: 22/8/2021 change color
            if (state == State.ERROR) {
                if (icon != null && errorColor != null) {
                    icon.mutate().setTint(errorColor);
                }
            } else {
                if (icon != null && normalColor != null) {
                    icon.mutate().setTint(normalColor);
                }
            }

            setImageDrawable(icon);

            if (icon instanceof Animatable) {
                ((Animatable) icon).start();
            }
        }

        this.state = state;
    }

    public int getNormalColor() {
        return normalColor;
    }

    public void setNormalColor(int normalColor) {
        this.normalColor = normalColor;
    }

    public int getErrorColor() {
        return errorColor;
    }

    public void setErrorColor(int errorColor) {
        this.errorColor = errorColor;
    }

    // Keep in sync with attrs.
    public enum State {
        OFF,
        ON,
        ERROR,
    }
}
