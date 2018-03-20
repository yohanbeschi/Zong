package musicxmltestsuite.tests.base;

import static com.xenoage.zong.core.text.UnformattedText.ut;

import com.xenoage.zong.core.music.lyric.Lyric;
import com.xenoage.zong.core.music.lyric.SyllableType;


public interface Base61d
	extends Base {

	@Override default String getFileName() {
		return "61d-Lyrics-Melisma.xml";
	}
	
	Lyric[][] expectedLyrics = {
		{ new Lyric(Companion.ut("Me"), SyllableType.Begin, 0) },
		{ },
		{ },
		{ },
		{ new Lyric(Companion.ut("lis"), SyllableType.Middle, 0) },
		{ },
		{ new Lyric(Companion.ut("ma."), SyllableType.End, 0) },
		{ },
	};

}
