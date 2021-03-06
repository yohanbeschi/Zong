package com.xenoage.zong.musiclayout.stampings;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.xenoage.utils.annotations.Const;
import com.xenoage.zong.core.music.barline.Barline;
import com.xenoage.zong.core.music.group.BarlineGroup;

/**
 * Class for a barline stamping.
 * 
 * The stamping can be placed on a single staff or a list of staves
 * (the barlines are connected).
 * 
 * A special case is a "Mensurstrich" barline,  which is placed between
 * the staves, but not on them.
 * 
 * At the moment a single stroke is used for the barline.
 *
 * @author Andreas Wenger
 */
@Const @AllArgsConstructor @Getter
public class BarlineStamping
	extends Stamping {

	/** The musical element, including the repeat and line style. */
	public final Barline barline;
	/** The list of staves this barline is spanning. */
	public final List<StaffStamping> staves;
	/** The horizontal position in mm, relative to the parent frame. */
	public final float xMm;
	/** The grouping style of the barline. */
	public final BarlineGroup.Style groupStyle;


	@Override public StampingType getType() {
		return StampingType.BarlineStamping;
	}
	
	@Override public Level getLevel() {
		return Level.Music;
	}

}
