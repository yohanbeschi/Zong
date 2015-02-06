package com.xenoage.zong.musiclayout.layouter;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import com.xenoage.utils.math.geom.Size2f;
import com.xenoage.zong.layout.frames.ScoreFrame;
import com.xenoage.zong.musiclayout.layouter.verticalframefilling.VerticalFrameFillingStrategy;
import com.xenoage.zong.musiclayout.spacer.system.fill.SystemFiller;

/**
 * An area in which a score should be layouted.
 * 
 * @author Andreas Wenger
 */
@AllArgsConstructor @RequiredArgsConstructor
public class ScoreLayoutArea {

	public final Size2f size;
	public SystemFiller hFill = ScoreFrame.defaultHFill;
	public VerticalFrameFillingStrategy vFill = ScoreFrame.defaultVFill;

}
