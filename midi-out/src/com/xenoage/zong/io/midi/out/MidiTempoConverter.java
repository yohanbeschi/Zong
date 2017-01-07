package com.xenoage.zong.io.midi.out;

import com.xenoage.utils.math.Fraction;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.header.ColumnHeader;
import com.xenoage.zong.core.music.direction.Tempo;
import com.xenoage.zong.core.music.util.BeatE;
import com.xenoage.zong.core.music.util.BeatEList;
import com.xenoage.zong.io.midi.out.repetitions.Repetitions;
import com.xenoage.zong.io.midi.out.repetitions.PlayRange;

import static com.xenoage.utils.kernel.Range.range;
import static com.xenoage.utils.math.Fraction._0;
import static com.xenoage.zong.io.midi.out.MidiConverter.calculateTickFromFraction;

/**
 * This class calculates the tempo changes for the {@link MidiConverter}.
 * 
 * @author Uli Teschemacher
 * @author Andreas Wenger
 */
public class MidiTempoConverter {

	/**
	 * Writes tempo changes into the given tempo track.
	 * Only tempos found in the {@link ColumnHeader}s are used.
	 */
	public static void writeTempoTrack(Score score, Repetitions repetitions, int resolution,
																		 MidiSequenceWriter<?> writer, int track) {
		long measurestarttick = 0;
		for (PlayRange playRange : repetitions.getRanges()) {
			for (int iMeasure : range(playRange.start.measure, playRange.end.measure)) {
				Fraction start = (playRange.start.measure == iMeasure ? playRange.start.beat : _0);
				Fraction end = (playRange.end.measure == iMeasure ?
					playRange.end.beat : score.getMeasureBeats(iMeasure));

				BeatEList<Tempo> tempos = score.getHeader().getColumnHeader(iMeasure).getTempos();
				if (tempos != null) {
					for (BeatE<Tempo> beatE : tempos) {
						long tick = measurestarttick +
							calculateTickFromFraction(beatE.beat.sub(start), resolution);
						writer.writeTempoChange(track, tick, beatE.getElement().getBeatsPerMinute());
					}
				}

				Fraction measureDuration = end.sub(start);
				measurestarttick += MidiConverter.calculateTickFromFraction(measureDuration, resolution);
			}
		}
	}

}
