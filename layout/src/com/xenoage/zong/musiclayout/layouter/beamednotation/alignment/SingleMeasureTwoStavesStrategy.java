package com.xenoage.zong.musiclayout.layouter.beamednotation.alignment;

import static com.xenoage.utils.iterators.It.it;
import static com.xenoage.zong.core.music.chord.StemDirection.Up;

import com.xenoage.utils.iterators.It;
import com.xenoage.zong.core.music.beam.Beam;
import com.xenoage.zong.core.music.beam.BeamWaypoint;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.chord.Stem;
import com.xenoage.zong.core.music.chord.StemDirection;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterStrategy;
import com.xenoage.zong.musiclayout.layouter.beamednotation.BeamedStemAlignmentNotationsStrategy;
import com.xenoage.zong.musiclayout.layouter.beamednotation.design.BeamDesign;
import com.xenoage.zong.musiclayout.layouter.beamednotation.design.DoubleBeamDesign;
import com.xenoage.zong.musiclayout.layouter.beamednotation.design.MultipleBeamDesign;
import com.xenoage.zong.musiclayout.layouter.beamednotation.design.SingleBeamDesign;
import com.xenoage.zong.musiclayout.layouter.beamednotation.design.TripleBeamDesign;
import com.xenoage.zong.musiclayout.notations.ChordNotation;
import com.xenoage.zong.musiclayout.notations.Notations;
import com.xenoage.zong.musiclayout.notations.beam.BeamStemAlignments;
import com.xenoage.zong.musiclayout.notations.chord.NotesNotation;
import com.xenoage.zong.musiclayout.notations.chord.StemNotation;

/**
 * This class helps the {@link BeamedStemAlignmentNotationsStrategy}
 * to compute beams that are all in the same measure, but in
 * two adjacent staves.
 * 
 * The strategy is quite simple: The first and the last stem have the default lengths
 * (or the lengths the user has defined). All other stem alignments can not be
 * computed here, since the distance of the staves is still unknown. They have to be
 * computed later, meanwhile they are null.
 * 
 * @author Andreas Wenger
 */
public class SingleMeasureTwoStavesStrategy
	implements ScoreLayouterStrategy {

	/**
	 * This strategy computes the lengths of the stems of the beamed chords.
	 */
	public void computeNotations(Beam beam, Notations notations) {
		NotesNotation[] chordNa = new NotesNotation[beam.getWaypoints().size()];
		int beamlines = beam.getMaxBeamLinesCount();
		int i = 0;
		for (BeamWaypoint waypoint : beam.getWaypoints()) {
			Chord chord = waypoint.getChord();
			ChordNotation cn = notations.getChord(chord);
			chordNa[i] = cn.notes;
			i++;
		}
		Chord firstChord = beam.getStart().getChord();
		Stem firstStem = firstChord.getStem();
		StemDirection firstStemDirection = notations.getChord(firstChord).stemDirection;
		Chord lastChord = beam.getStop().getChord();
		Stem lastStem = lastChord.getStem();
		StemDirection lastStemDirection = notations.getChord(lastChord).stemDirection;

		BeamStemAlignments bsa = computeStemAlignments(chordNa, beamlines, firstStem, lastStem,
			firstStemDirection, lastStemDirection);

		//compute new notations
		It<BeamWaypoint> waypoints = it(beam.getWaypoints());
		for (BeamWaypoint waypoint : waypoints) {
			Chord chord = waypoint.getChord();
			ChordNotation cn = notations.getChord(chord);
			cn.stem = bsa.stemAlignments[waypoints.getIndex()];
		}
	}

	/**
	 * Computes the vertical positions of all stems
	 * of the given beam. The lengths of the middle stems have to be
	 * recomputed in a later step, since their lengths can not be computed yet.
	 * 
	 * @param chordNa             the alignments of all chords of the beam
	 * @param beamLinesCount      the number of lines of the beam
	 * @param stemDirection       the direction of the stem
	 * @param firstStem           the stem of the first chord
	 * @param lastStem            the stem of the last chord
	 * @param firstStemDirection  the direction of the first chord
	 * @param lastStemDirection   the direction of the last chord
	 * @return  the alignments of all stems of the given chords                        
	 */
	public BeamStemAlignments computeStemAlignments(NotesNotation[] chordNa, int beamLinesCount,
		Stem firstStem, Stem lastStem, StemDirection firstStemDirection, StemDirection lastStemDirection) {
		//get appropriate beam design
		BeamDesign beamDesign;
		switch (beamLinesCount) {
		//TIDY: we need only a small subset of the BeamDesign class. extract it?
			case 1:
				beamDesign = new SingleBeamDesign(firstStemDirection, 0);
				break;
			case 2:
				beamDesign = new DoubleBeamDesign(firstStemDirection, 0);
				break;
			case 3:
				beamDesign = new TripleBeamDesign(firstStemDirection, 0);
				break;
			default:
				beamDesign = new MultipleBeamDesign(firstStemDirection, 0, beamLinesCount);
		}

		//compute stem alignments
		int chordsCount = chordNa.length;
		StemNotation[] stemAlignments = new StemNotation[chordsCount];
		for (int i = 0; i < chordsCount; i++) {

			Stem stem = (i == 0 ? firstStem : lastStem);
			StemDirection stemDirection = (i == 0 ? firstStemDirection : lastStemDirection);

			//start LP
			float startLP;
			if (stemDirection == Up) {
				startLP = chordNa[i].getLps().getBottom();
			}
			else {
				startLP = chordNa[i].getLps().getTop();
			}

			//end LP
			float endLP;
			if (stem.getLength() != null) {
				//use user-defined length
				endLP = startLP + stemDirection.getSign() * 2 * stem.getLength();
			}
			else {
				//compute length
				endLP = startLP + stemDirection.getSign() * 2 * beamDesign.getMinimumStemLength();
			}

			stemAlignments[i] = new StemNotation(startLP, endLP);
		}
		BeamStemAlignments beamstemalignments = new BeamStemAlignments(stemAlignments,
			BeamDesign.BEAMLINE_WIDTH, beamDesign.getDistanceBetweenBeamLines(), beamLinesCount);

		return beamstemalignments;
	}

}
