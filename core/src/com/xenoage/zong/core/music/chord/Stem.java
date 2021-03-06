package com.xenoage.zong.core.music.chord;

import lombok.Data;

import com.xenoage.utils.annotations.Const;
import com.xenoage.utils.annotations.MaybeNull;
import com.xenoage.utils.annotations.NonNull;


/**
 * Class for a stem, that is belongs to a chord.
 *
 * @author Andreas Wenger
 */
@Const @Data public final class Stem {
	
	public static final Stem defaultStem = new Stem(StemDirection.Default, null);

	/** The direction of the stem, or null for default. */
	@NonNull private final StemDirection direction;

	/** The length of the stem, measured from the outermost chord note at the stem side
	 * to the end of the stem, in interline spaces, or null for default. For example,
	 * a stem length of 3.5 IS is a common value for unbeamed chords. */
	@MaybeNull private final Float lengthIs;

}
