package musicxmltestsuite.tests.base;

import static com.xenoage.utils.math.Fraction.fr;
import musicxmltestsuite.tests.utils.ToDo;

import com.xenoage.utils.math.Fraction;


@ToDo("add layout test for this case. notes must be properly aligned.")
public interface Base23f
	extends Base {

	@Override default String getFileName() {
		return "23f-Tuplets-DurationButNoBracket.xml";
	}
	
	Fraction[][] expectedDurations = {
		{ //staff 0
			Companion.fr(1, 4), Companion.fr(1, 4), Companion.fr(1, 6), Companion.fr(1, 6), Companion.fr(1, 6)
		},
		{ //staff 1
			Companion.fr(1, 8), Companion.fr(1, 8), Companion.fr(1, 12), Companion.fr(1, 12), Companion.fr(1, 12),
			Companion.fr(1, 16), Companion.fr(1, 16), Companion.fr(1, 16), Companion.fr(1, 16),
			Companion.fr(1, 24), Companion.fr(1, 24), Companion.fr(1, 24), Companion.fr(1, 24), Companion.fr(1, 24), Companion.fr(1, 24)
		}
	};

}
