package com.xenoage.zong.utils.demo;

import static com.xenoage.utils.collections.CollectionUtils.addOrNew;
import static com.xenoage.utils.collections.CollectionUtils.alist;
import static com.xenoage.utils.math.Fraction._0;
import static com.xenoage.utils.math.Fraction.fr;
import static com.xenoage.zong.core.music.Pitch.A;
import static com.xenoage.zong.core.music.Pitch.B;
import static com.xenoage.zong.core.music.Pitch.C;
import static com.xenoage.zong.core.music.Pitch.D;
import static com.xenoage.zong.core.music.Pitch.E;
import static com.xenoage.zong.core.music.Pitch.F;
import static com.xenoage.zong.core.music.Pitch.G;
import static com.xenoage.zong.core.music.Pitch.pi;
import static com.xenoage.zong.core.music.format.SP.sp;
import static com.xenoage.zong.core.position.MP.mp;
import static com.xenoage.zong.core.text.UnformattedText.ut;

import java.util.ArrayList;

import com.xenoage.utils.math.Fraction;
import com.xenoage.zong.commands.core.music.ColumnElementWrite;
import com.xenoage.zong.commands.core.music.PartAdd;
import com.xenoage.zong.commands.core.music.direction.DirectionAdd;
import com.xenoage.zong.commands.core.music.group.BarlineGroupAdd;
import com.xenoage.zong.commands.core.music.group.BracketGroupAdd;
import com.xenoage.zong.commands.core.music.slur.SlurAdd;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.format.StaffLayout;
import com.xenoage.zong.core.instrument.Instrument;
import com.xenoage.zong.core.music.ColumnElement;
import com.xenoage.zong.core.music.MeasureElement;
import com.xenoage.zong.core.music.MeasureSide;
import com.xenoage.zong.core.music.Part;
import com.xenoage.zong.core.music.Pitch;
import com.xenoage.zong.core.music.annotation.Annotation;
import com.xenoage.zong.core.music.annotation.Articulation;
import com.xenoage.zong.core.music.annotation.ArticulationType;
import com.xenoage.zong.core.music.barline.Barline;
import com.xenoage.zong.core.music.barline.BarlineStyle;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.chord.Note;
import com.xenoage.zong.core.music.clef.Clef;
import com.xenoage.zong.core.music.clef.ClefType;
import com.xenoage.zong.core.music.direction.Dynamic;
import com.xenoage.zong.core.music.direction.DynamicValue;
import com.xenoage.zong.core.music.direction.Tempo;
import com.xenoage.zong.core.music.direction.Wedge;
import com.xenoage.zong.core.music.direction.WedgeType;
import com.xenoage.zong.core.music.format.BezierPoint;
import com.xenoage.zong.core.music.format.Position;
import com.xenoage.zong.core.music.group.BarlineGroup;
import com.xenoage.zong.core.music.group.BracketGroup;
import com.xenoage.zong.core.music.group.StavesRange;
import com.xenoage.zong.core.music.key.TraditionalKey;
import com.xenoage.zong.core.music.key.TraditionalKey.Mode;
import com.xenoage.zong.core.music.rest.Rest;
import com.xenoage.zong.core.music.slur.Slur;
import com.xenoage.zong.core.music.slur.SlurType;
import com.xenoage.zong.core.music.slur.SlurWaypoint;
import com.xenoage.zong.core.music.time.TimeSignature;
import com.xenoage.zong.core.music.time.TimeType;
import com.xenoage.zong.core.position.MP;
import com.xenoage.zong.io.selection.Cursor;


/**
 * This class creates a demo score
 * (Revolutionary Etude op. 10/12 Chopin).
 * 
 * @author Uli Teschemacher
 * @author Andreas Wenger
 */
public class ScoreRevolutionary {

	public static Score createScore() {
		Score score = new Score();
		
		Instrument instr = Instrument.Companion.getDefaultInstrument();
		float is = score.getFormat().getInterlineSpace();
		StaffLayout staffLayout = new StaffLayout(is * 9);
		score.getFormat().setStaffLayoutOther(staffLayout);

		ArticulationType[] accent = { ArticulationType.Accent };
		ArticulationType[] staccato = { ArticulationType.Staccato };

		Fraction f2 = Companion.fr(1, 2);
		Fraction f4 = Companion.fr(1, 4);
		Fraction f8 = Companion.fr(1, 8);
		Fraction f16 = Companion.fr(1, 16);

		Chord attachC, firstSlurC, lastSlurC;
		BezierPoint firstSlurB, lastSlurB;

		Part pianoPart = new Part("Piano", null, 2, alist(instr));
		new PartAdd(score, pianoPart, 0, null).execute();

		//set barlines and brackets
		new BarlineGroupAdd(score.getStavesList(), new BarlineGroup(new StavesRange(0, 1), BarlineGroup.Style.Common)).execute();
		new BracketGroupAdd(score.getStavesList(), new BracketGroup(new StavesRange(0, 1), BracketGroup.Style.Bracket)).execute();

		//use cursor for more convenient input
		Cursor cursor = new Cursor(score, MP.mp0, true);

		//C minor, C (4/4) time
		cursor.write((ColumnElement) new TraditionalKey(-3, Mode.Minor));
		cursor.write(new TimeSignature(TimeType.Companion.getTimeCommon()));

		//first staff: g-clef and some notes
		cursor.write(new Clef(ClefType.Companion.getClefTreble()));

		//measure 1
		Tempo tempo = new Tempo(f4, 160); //, , FontInfo.defaultValue, );
		tempo.setText(Companion.ut("Allegro con fuoco."));
		tempo.setPositioning(new Position(null, 22f, -5f, -5f));
		cursor.write((ColumnElement) tempo);
		cursor.write(attachC = chord(f2, accent, Companion.pi(Companion.getB(), 4), Companion.pi(Companion.getD(), 5), Companion.pi(Companion.getF(), 5), Companion.pi(Companion.getG(), 5), Companion.pi(Companion.getB(), 5)));
		attachC.setDirections(addOrNew(attachC.getDirections(), new Dynamic(DynamicValue.f)));
		cursor.write(new Rest(f2));

		//measure 2
		cursor.write(new Rest(f2));
		cursor.write(new Rest(f4));
		Wedge cresc = new Wedge(WedgeType.Crescendo);
		cresc.setPositioning(new Position(null, null, -1f, -2f));
		cursor.write((MeasureElement) cresc);
		cursor.openBeam();
		cursor.write(firstSlurC = chord(Companion.fr(3, 16), accent, Companion.pi(Companion.getA(), -1, 4), Companion.pi(Companion.getE(), -1, 5), Companion.pi(Companion.getF(), 5), Companion.pi(Companion.getA(), -1, 5)));
		cursor.write(lastSlurC = chord(f16, Companion.pi(Companion.getG(), 4), Companion.pi(Companion.getG(), 5)));
		cursor.closeBeam();
		cursor.write((MeasureElement) cresc.getWedgeEnd());
		firstSlurB = new BezierPoint(sp(is * 0.8f, is * 7.6f), sp(is, is * 0.8f));
		lastSlurB = new BezierPoint(sp(0, is * 6f), sp(-is, is));
		new SlurAdd(new Slur(SlurType.Slur, clwp(firstSlurC, firstSlurB), clwp(lastSlurC, lastSlurB), null)).execute();

		//measure 3
		cursor.write(attachC = chord(f2, Companion.pi(Companion.getD(), 5), Companion.pi(Companion.getF(), 5), Companion.pi(Companion.getG(), 5), Companion.pi(Companion.getD(), 6)));
		new DirectionAdd(new Dynamic(DynamicValue.f), attachC).execute();
		cursor.write(new Rest(f2));

		//measure 4
		cursor.write(new Rest(f2));
		cursor.write(new Rest(f4));
		cresc = new Wedge(WedgeType.Crescendo);
		cresc.setPositioning(new Position(null, null, -1f, -2f));
		cursor.write((MeasureElement) cresc);
		cursor.openBeam();
		cursor.write(firstSlurC = chord(Companion.fr(3, 16), accent, Companion.pi(Companion.getA(), -1, 4), Companion.pi(Companion.getE(), -1, 5), Companion.pi(Companion.getF(), 0, 5), Companion.pi(Companion.getA(), -1, 5)));
		cursor.write(lastSlurC = chord(f16, Companion.pi(Companion.getG(), 0, 4), Companion.pi(Companion.getG(), 0, 5)));
		cursor.closeBeam();
		cursor.write((MeasureElement) cresc.getWedgeEnd());
		firstSlurB = new BezierPoint(sp(is * 0.8f, is * 7.6f), sp(is, is * 0.8f));
		lastSlurB = new BezierPoint(sp(0, is * 6f), sp(-is, is));
		new SlurAdd(new Slur(SlurType.Slur, clwp(firstSlurC, firstSlurB), clwp(lastSlurC, lastSlurB), null)).execute();

		//measure 5
		cursor.write(attachC = chord(f4, staccato, Companion.pi(Companion.getF(), 5), Companion.pi(Companion.getG(), 5), Companion.pi(Companion.getD(), 6), Companion.pi(Companion.getF(), 6)));
		new DirectionAdd(new Dynamic(DynamicValue.f), attachC).execute();
		cursor.write(new Rest(f4));
		cursor.write(new Rest(f2));

		//second staff: f-clef some notes
		cursor = new Cursor(cursor.getScore(), mp(1, 0, 0, Companion.get_0(), 0), true);
		cursor.write(new Clef(ClefType.Companion.getClefBass()));

		//measure 1
		cursor.openBeam();
		cursor.write(new Rest(f8));
		cursor.write(firstSlurC = chord(f16, Companion.pi(Companion.getA(), -1, 4)));
		cursor.write(chord(f16, Companion.pi(Companion.getG(), 0, 4)));
		cursor.closeBeam();

		cursor.openBeam();
		cursor.write(chord(f16, accent, Companion.pi(Companion.getF(), 0, 4)));
		cursor.write(chord(f16, Companion.pi(Companion.getD(), 0, 4)));
		cursor.write(chord(f16, Companion.pi(Companion.getE(), -1, 4)));
		cursor.write(chord(f16, Companion.pi(Companion.getD(), 0, 4)));
		cursor.closeBeam();

		cursor.openBeam();
		cursor.write(chord(f16, accent, Companion.pi(Companion.getB(), 0, 3)));
		cursor.write(chord(f16, Companion.pi(Companion.getG(), 0, 3)));
		cursor.write(chord(f16, Companion.pi(Companion.getA(), -1, 3)));
		cursor.write(chord(f16, Companion.pi(Companion.getG(), 0, 3)));
		cursor.closeBeam();

		cursor.openBeam();
		cursor.write(chord(f16, accent, Companion.pi(Companion.getF(), 0, 3)));
		cursor.write(chord(f16, Companion.pi(Companion.getD(), 0, 3)));
		cursor.write(chord(f16, Companion.pi(Companion.getE(), -1, 3)));
		cursor.write(chord(f16, Companion.pi(Companion.getD(), 0, 3)));
		cursor.closeBeam();

		//measure 2
		cursor.openBeam();
		cursor.write(chord(f16, accent, Companion.pi(Companion.getB(), 0, 2)));
		cursor.write(chord(f16, Companion.pi(Companion.getG(), 0, 2)));
		cursor.write(chord(f16, Companion.pi(Companion.getA(), -1, 2)));
		cursor.write(chord(f16, Companion.pi(Companion.getG(), 0, 2)));
		cursor.closeBeam();

		cursor.openBeam();
		cursor.write(chord(f16, accent, Companion.pi(Companion.getF(), 0, 2)));
		cursor.write(chord(f16, Companion.pi(Companion.getD(), 0, 2)));
		cursor.write(chord(f16, Companion.pi(Companion.getE(), -1, 2)));
		cursor.write(chord(f16, Companion.pi(Companion.getD(), 0, 2)));
		cursor.closeBeam();

		cursor.openBeam();
		cursor.write(chord(f16, accent, Companion.pi(Companion.getC(), 0, 2)));
		cursor.write(chord(f16, Companion.pi(Companion.getG(), 0, 1)));
		cursor.write(chord(f16, Companion.pi(Companion.getC(), 0, 2)));
		cursor.write(chord(f16, Companion.pi(Companion.getG(), 0, 1)));
		cursor.closeBeam();

		cursor.openBeam();
		cursor.write(chord(f16, accent, Companion.pi(Companion.getC(), 0, 2)));
		cursor.write(chord(f16, Companion.pi(Companion.getG(), 0, 1)));
		cursor.write(chord(f16, Companion.pi(Companion.getC(), 0, 2)));
		cursor.write(lastSlurC = chord(f16, Companion.pi(Companion.getG(), 0, 1)));
		cursor.closeBeam();
		firstSlurB = new BezierPoint(sp(0, is * 1.5f), sp(15, 5));
		lastSlurB = new BezierPoint(sp(0, is * 7.5f), sp(-is * 5, is * 2));
		new SlurAdd(new Slur(SlurType.Slur, clwp(firstSlurC, firstSlurB), clwp(lastSlurC, lastSlurB), null)).execute();

		//measure 3
		cursor.write(chord(f8, staccato, Companion.pi(Companion.getB(), 0, 1)));
		cursor.openBeam();
		cursor.write(firstSlurC = chord(f16, Companion.pi(Companion.getA(), -1, 4)));
		cursor.write(chord(f16, Companion.pi(Companion.getG(), 0, 4)));
		cursor.closeBeam();

		cursor.openBeam();
		cursor.write(chord(f16, accent, Companion.pi(Companion.getF(), 0, 4)));
		cursor.write(chord(f16, Companion.pi(Companion.getD(), 0, 4)));
		cursor.write(chord(f16, Companion.pi(Companion.getE(), -1, 4)));
		cursor.write(chord(f16, Companion.pi(Companion.getD(), 0, 4)));
		cursor.closeBeam();

		cursor.openBeam();
		cursor.write(chord(f16, accent, Companion.pi(Companion.getB(), 0, 3)));
		cursor.write(chord(f16, Companion.pi(Companion.getG(), 0, 3)));
		cursor.write(chord(f16, Companion.pi(Companion.getA(), -1, 3)));
		cursor.write(chord(f16, Companion.pi(Companion.getG(), 0, 3)));
		cursor.closeBeam();

		cursor.openBeam();
		cursor.write(chord(f16, accent, Companion.pi(Companion.getF(), 0, 3)));
		cursor.write(chord(f16, Companion.pi(Companion.getD(), 0, 3)));
		cursor.write(chord(f16, Companion.pi(Companion.getE(), -1, 3)));
		cursor.write(lastSlurC = chord(f16, Companion.pi(Companion.getD(), 0, 3)));
		cursor.closeBeam();
		firstSlurB = new BezierPoint(sp(0, is * 1.5f), sp(15, 3));
		lastSlurB = new BezierPoint(sp(0, is * 5f), sp(-is * 5.5f, is * 2));
		new SlurAdd(new Slur(SlurType.Slur, clwp(firstSlurC, firstSlurB), clwp(lastSlurC, lastSlurB), null)).execute();

		//measure 4
		cursor.openBeam();
		cursor.write(firstSlurC = chord(f16, accent, Companion.pi(Companion.getB(), 0, 2)));
		cursor.write(chord(f16, Companion.pi(Companion.getG(), 0, 2)));
		cursor.write(chord(f16, Companion.pi(Companion.getA(), -1, 2)));
		cursor.write(chord(f16, Companion.pi(Companion.getG(), 0, 2)));
		cursor.closeBeam();

		cursor.openBeam();
		cursor.write(chord(f16, accent, Companion.pi(Companion.getF(), 0, 2)));
		cursor.write(chord(f16, Companion.pi(Companion.getD(), 0, 2)));
		cursor.write(chord(f16, Companion.pi(Companion.getE(), -1, 2)));
		cursor.write(chord(f16, Companion.pi(Companion.getD(), 0, 2)));
		cursor.closeBeam();

		cursor.openBeam();
		cursor.write(chord(f16, accent, Companion.pi(Companion.getC(), 0, 2)));
		cursor.write(chord(f16, Companion.pi(Companion.getG(), 0, 1)));
		cursor.write(chord(f16, Companion.pi(Companion.getC(), 0, 2)));
		cursor.write(chord(f16, Companion.pi(Companion.getG(), 0, 1)));
		cursor.closeBeam();

		cursor.openBeam();
		cursor.write(chord(f16, accent, Companion.pi(Companion.getC(), 0, 2)));
		cursor.write(chord(f16, Companion.pi(Companion.getG(), 0, 1)));
		cursor.write(chord(f16, Companion.pi(Companion.getC(), 0, 2)));
		cursor.write(lastSlurC = chord(f16, Companion.pi(Companion.getG(), 0, 1)));
		cursor.closeBeam();
		firstSlurB = new BezierPoint(sp(-is, is * 8.5f), sp(15, 4));
		lastSlurB = new BezierPoint(sp(0, is * 7.5f), sp(-is * 5, is * 2));
		new SlurAdd(new Slur(SlurType.Slur, clwp(firstSlurC, firstSlurB), clwp(lastSlurC, lastSlurB), null)).execute();

		//measure 5
		cursor.write(chord(f4, staccato, Companion.pi(Companion.getB(), 0, 1)));
		cursor.write(new Rest(f4));
		cursor.write(new Rest(f2));

		//end line
		Barline barlineEnd = Barline.Companion.barline(BarlineStyle.LightHeavy);
		new ColumnElementWrite(barlineEnd, score.getColumnHeader(4), null, MeasureSide.Right).execute();

		return score;
	}


	private static Chord chord(Fraction fraction, Pitch... pitches) {
		return chord(fraction, null, pitches);
	}


	private static Chord chord(Fraction fraction, ArticulationType[] articulations, Pitch... pitches) {
		Chord chord = new Chord(Note.Companion.notes(pitches), fraction);
		if (articulations != null) {
			ArrayList<Annotation> a = alist(articulations.length);
			for (ArticulationType at : articulations)
				a.add(new Articulation(at));
			chord.setAnnotations(a);
		}
		return chord;
	}


	private static SlurWaypoint clwp(Chord c, BezierPoint bezierPoint) {
		return new SlurWaypoint(c, null, bezierPoint);
	}


}
