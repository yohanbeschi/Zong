package com.xenoage.zong.musiclayout.spacing;

import com.xenoage.utils.annotations.Optimized;
import com.xenoage.utils.kernel.Range;
import com.xenoage.utils.math.Fraction;
import com.xenoage.zong.core.position.MP;
import com.xenoage.zong.core.position.Time;
import com.xenoage.zong.musiclayout.SLP;
import lombok.Getter;

import java.util.List;

import static com.xenoage.utils.NullUtils.notNull;
import static com.xenoage.utils.annotations.Optimized.Reason.Performance;
import static com.xenoage.utils.collections.CollectionUtils.getFirst;
import static com.xenoage.utils.collections.CollectionUtils.getLast;
import static com.xenoage.utils.kernel.Range.range;
import static com.xenoage.zong.core.position.MP.*;

/**
 * The horizontal and vertical spacing of a system.
 * 
 * It contains the indices of the first and last measure of the system, the widths of all
 * measure columns, the width of the system (which may be longer than the width used
 * by the measures), the distances between the staffStampings and the vertical offset of the system.
 *
 * @author Andreas Wenger
 */
@Getter
public class SystemSpacing {

	/** The list of the spacings of the measure columns in this system. */
	public List<ColumnSpacing> columns;

	/** The left margin of the system in mm. */
	public float marginLeftMm;
	/** The right margin of the system in mm. */
	public float marginRightMm;

	/** The width of the system (without the horizontal offset).
	 * It may be longer than the used width, e.g. to create empty staves.
	 * To get the used width, call {@link #getUsedWidth()}. */
	public float widthMm;

	/** The vertical spacing of the staves. */
	public StavesSpacing staves;

	/** The vertical offset of the system in mm, relative to the top. */
	public float offsetYMm;

	/** Backward reference to the frame. */
	public FrameSpacing parentFrame = null;
	
	//the horizontal offsets of the columns
	@Optimized(Performance) private float[] cacheColumnsXMm;
	

	public SystemSpacing(List<ColumnSpacing> columnSpacings, float marginLeftMm, float marginRightMm,
		float widthMm, StavesSpacing staves, float offsetYMm) {
		this.columns = columnSpacings;
		this.marginLeftMm = marginLeftMm;
		this.marginRightMm = marginRightMm;
		this.widthMm = widthMm;
		this.staves = staves;
		this.offsetYMm = offsetYMm;
		onColumnsWidthChange();
		//set backward references
		for (ColumnSpacing column : columnSpacings)
			column.parentSystem = this;
	}
	
	/**
	 * Gets the height of the staff with the given index.
	 */
	public float getStaffHeightMm(int staff) {
		return staves.getStaffHeightMm(staff);
	}

	/**
	 * Gets the distance between the previous and the given staff.
	 */
	public float getStaffDistanceMm(int staff) {
		return staves.getStaffDistanceMm(staff);
	}

	/**
	 * Gets the total height of this system in mm.
	 */
	public float getHeightMm() {
		return staves.getTotalHeightMm();
	}

	/**
	 * Gets the used width of the system.
	 */
	public float getUsedWidth() {
		float usedWidth = 0;
		for (ColumnSpacing mcs : columns)
			usedWidth += mcs.getWidthMm();
		return usedWidth;
	}
	
	public int getStartMeasure() {
		return getFirst(columns).measureIndex;
	}
	
	public int getEndMeasure() {
		return getLast(columns).measureIndex;
	}
	
	public boolean containsMeasure(int scoreMeasure) {
		return getStartMeasure() <= scoreMeasure && scoreMeasure <= getEndMeasure();
	}
	
	public Range getMeasures() {
		return range(getStartMeasure(), getEndMeasure());
	}
	
	public ColumnSpacing getColumn(int scoreMeasure) {
		return columns.get(scoreMeasure - getStartMeasure());
	}
	
	public int getSystemIndexInFrame() {
		return parentFrame.systems.indexOf(this);
	}
	
	/**
	 * Gets the start position in mm of the measure with the given global index
	 * relative to the beginning of the system.
	 */
	public float getMeasureStartMm(int scoreMeasure) {
		float x = 0;
		for (int iMeasure : range(scoreMeasure - getStartMeasure()))
			x += columns.get(iMeasure).getWidthMm();
		return x;
	}
	
	/**
	 * Gets the end position of the leading spacing in mm of the measure with the given
	 * global index, relative to the beginning of the system.
	 * If there is no leading spacing, this value is equal to {@link #getMeasureStartMm(int)}
	 */
	public float getMeasureStartAfterLeadingMm(int scoreMeasure) {
		int systemMeasure = scoreMeasure - getStartMeasure();
		return getMeasureStartMm(scoreMeasure) + columns.get(systemMeasure).getLeadingWidthMm();
	}
	
	/**
	 * Gets the end position in mm of the measure with the given global index
	 * relative to the beginning of the system.
	 */
	public float getMeasureEndMm(int scoreMeasure) {
		int systemMeasure = scoreMeasure - getStartMeasure();
		return getMeasureStartMm(scoreMeasure) + columns.get(systemMeasure).getWidthMm();
	}

	/**
	 * Gets the {@link MP} at the given horizontal position in mm.
	 * If the given staff is {@link MP#unknown}, all beats of the column
	 * are used, otherwise only the beats used by the given staff.
	 * 
	 * If it is between two beats (which will be true almost ever), the
	 * the right mark is selected (like it is usual e.g. in text
	 * processing applications). If it is behind all known beats of the
	 * hit measure, the last known beat is returned.
	 * 
	 * If it is not within the boundaries of a measure, {@link MP#unknownMp} is returned.
	 */
	public MP getMpAt(float xMm, int staff) {
		//find the measure
		int measureIndex = getSystemMeasureIndexAt(xMm);
		float xMmInMeasure = xMm - getMeasureStartMm(measureIndex);
		//when measure was not found, return null
		if (measureIndex == unknown)
			return unknownMp;
		//get the beat at the given position
		Fraction beat = columns.get(measureIndex).getBeatAt(xMmInMeasure, staff);
		return atBeat(staff, measureIndex, unknown, beat);
	}
	
	/**
	 * Gets the horizontal position in mm, relative to the beginning of the staff,
	 * of the given time.
	 * If the given beat is after the last beat, the offset of the last beat is returned.
	 */
	public float getXMmAt(Time time) {
		float measureXMm = getMeasureStartMm(time.getMeasure());
		float elementXMm = columns.get(time.getMeasure() - getStartMeasure()).getXMmAt(time.getBeat());
		return measureXMm + elementXMm;
	}

	/**
	 * Gets the interline space of the staff with the given index.
	 */
	public float getInterlineSpace(int staff) {
		return notNull(staves.getStaves().get(staff).getInterlineSpace(), staves.getDefaultIs());
	}

	/**
	 * Gets the system-relative index of the measure at the given position in mm,
	 * or {@link MP#unknown} if there is none.
	 */
	private int getSystemMeasureIndexAt(float xMm) {
		if (xMm < 0)
			return unknown;
		float x = 0;
		for (int iMeasure : range(columns)) {
			x += columns.get(iMeasure).getWidthMm();
			if (xMm < x)
				return iMeasure;
		}
		return unknown;
	}
	
	/**
	 * Gets the vertical offset of the given staff in mm in frame space.
	 */
	public float getStaffYOffsetMm(int staff) {
		return offsetYMm + staves.getStaffYOffsetMm(staff);
	}
	
	/** 
	 * Computes and returns the y-coordinate in mm in frame space
	 * of an object on the given {@link SLP}.
	 */
	public float getYMm(SLP slp) {
		return offsetYMm + staves.getYMm(slp);
	}
	
	/**
	 * Computes and returns the y-coordinate of an object in the given staff 
	 * at the given vertical position in mm in frame space as a line position.
	 */
	public float getLp(int staff, float mm) {
		return staves.getLp(staff, mm - offsetYMm);
	}
	
	/**
	 * Call this method when the width of a column has been changed.
	 */
	public void onColumnsWidthChange() {
		//recompute cache
		if (cacheColumnsXMm == null || cacheColumnsXMm.length != columns.size())
			cacheColumnsXMm = new float[columns.size()];
		float xMm = 0;
		for (int measure : range(columns)) {
			ColumnSpacing column = columns.get(measure);
			cacheColumnsXMm[measure] = xMm;
			xMm += column.getWidthMm();
		}
	}
	
	/**
	 * Gets the horizontal offset in mm of the measure with the given global index.
	 */
	public float getColumnXMm(int scoreMeasure) {
		return cacheColumnsXMm[scoreMeasure - getStartMeasure()];
	}

}
